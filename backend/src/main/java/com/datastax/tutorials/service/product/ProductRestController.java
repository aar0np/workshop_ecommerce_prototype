package com.datastax.tutorials.service.product;

import com.datastax.tutorials.service.dataapi.DataAPIServices;
import com.datastax.tutorials.service.dataapi.entities.OrderByUserTableEntity;
import com.datastax.tutorials.service.dataapi.entities.OrderTableEntity;
import com.datastax.tutorials.service.dataapi.entities.ProductVectorsTableEntity;
import com.datastax.tutorials.service.order.OrderByUserRepository;
import com.datastax.tutorials.service.order.OrderProduct;
import com.datastax.tutorials.service.order.OrderProductComparator;
import com.datastax.tutorials.service.order.OrderRepository;
import com.datastax.tutorials.service.order.OrderByUserEntity;
import com.datastax.tutorials.service.order.OrderEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Expose Rest Api to interact with products.
 *
 * @author Cedrick LUNVEN
 * @author Aaron PLOETZ
 */
@RestController
@CrossOrigin(
        methods = {POST,GET,OPTIONS,PUT,DELETE,PATCH}, 
        maxAge = 3600, 
        allowedHeaders = {"x-requested-with","origin","content-type","accept"}, 
        origins = "*")
@Tag(   name = "Product Service", 
        description = "Provide crud operations for Products")
@RequestMapping("/api/v1/products/")
public class ProductRestController {

    /** Inject the repository. */
    private DataAPIServices dataApiServices;
	private OrderRepository orderRepo;
	private OrderByUserRepository orderUserRepo;
    // Need separate DAL for vector-related queries
    // ...for now.
    //private ProductVectorDAL vectorDAL;
    
    /**
     * Injection through constructor.
     *  
     * @param repo
     *      repository
     */
    public ProductRestController(DataAPIServices dataApiService,
    		OrderRepository oRepo, OrderByUserRepository oURepo) {
        this.dataApiServices = dataApiService;
        this.orderRepo = oRepo;
        this.orderUserRepo = oURepo;
    }
    
    @GetMapping("/product/{productid}")
    @Operation(summary = "Retrieve product details from its id", 
               description = "Find product detailsfrom its id `SELECT * FROM PRODUCT WHERE product_id =?`", 
               responses = {
            @ApiResponse(responseCode = "200", description = "A product", 
                         content = @Content(mediaType = "application/json", 
                         schema  = @Schema(implementation = Product.class, name = "Product"))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter check productId format."),
            @ApiResponse(responseCode = "500", description = "Technical Internal error.")}
    )
    public ResponseEntity<Product> findByProductId(HttpServletRequest req,
            @PathVariable(value = "productid") 
            @Parameter(name = "productid", description = "Product identifier", example = "LS5342XL") 
            String productid) {
    	//Optional<ProductEntity> pe = productRepo.findById(productid);
        return dataApiServices
                .findProductById(productid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
	@GetMapping("/promoproduct/{productid}")
    @Operation(summary = "Retrieve product vector from its id", 
    description = "Find product vector by id `SELECT * FROM pet_supply_vectors ORDER BY product_vector ANN OF ? LIMIT 8`", 
    responses = {
    		@ApiResponse(responseCode = "200", description = "A product by vector", 
              content = @Content(mediaType = "application/json", 
              schema  = @Schema(implementation = Product.class, name = "Promotion Product"))),
    		@ApiResponse(responseCode = "404", description = "Product not found"),
    		@ApiResponse(responseCode = "400", description = "Invalid parameter check productId format."),
    		@ApiResponse(responseCode = "500", description = "Technical Internal error.")}
    )
	public ResponseEntity<ProductVector> getPromotionProduct(HttpServletRequest req,
            @PathVariable(value = "productid") 
            String productid) {

		// get original product's vector
		Optional<ProductVectorsTableEntity> originalProduct = 
				dataApiServices.findProductVectorById(productid);
		
		if (!originalProduct.isEmpty()) {

			// product exists, now query by its vector to get the closest 8 product matches
			List<ProductVectorsTableEntity> entityList =
					dataApiServices.findProductsByVector(originalProduct.get().getProductVector(), 8);
			
			// convert entity results to List of POJO
			List<ProductVector> ann = new ArrayList<ProductVector>();
			for (ProductVectorsTableEntity pve : entityList) {
				ann.add(mapProductVector(pve));
			}
			
			if (ann.size() > 1) {

				for (ProductVector product : ann) {
					String prodGroup = product.getProductGroup();
					// The closest matches for clothing will likely just have different sizes.
					// So, we will iterate through the list until we find the first product with a
					// different product group.
					
					if (!prodGroup.equals(originalProduct.get().getGroup())) {

						return ResponseEntity.ok(product);
					}
				}
			}			
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping("/promoproduct/{userid}/customer")
	public ResponseEntity<List<ProductVector>> getCustomerPromotionProducts(HttpServletRequest req,
			@PathVariable(value = "userid") 
			UUID userid) {
				
		// get user's orders
		//List<OrderByUserTableEntity> userOrders = dataApiServices.getOrdersByUser(userid);
		Optional<List<OrderByUserEntity>> userOrders = orderUserRepo.findByKeyUserId(userid);

		if (userOrders.isEmpty()) {
			// no orders found for user
			return ResponseEntity.notFound().build();
		}
		
		List<OrderProduct> uniqueProducts = new ArrayList<OrderProduct>();
		
		// get unique product with total quantities
		for (OrderByUserEntity userOrder : userOrders.get()) {
			
			// get products from order
			//List<OrderTableEntity> orderProducts =
			//		dataApiServices.getOrderById(userOrder.getKey().getOrderId());
			List<OrderEntity> orderProducts =
					orderRepo.findByKeyOrderId(userOrder.getKey().getOrderId());
			
			for (OrderEntity orderProduct : orderProducts) {
				OrderProduct uniqueProduct = 
						isProductInList(uniqueProducts, orderProduct);
				if (uniqueProduct != null) {
					// product already exists, just update the quantity
					uniqueProduct.setProductQty(uniqueProduct.getProductQty() + orderProduct.getProductQty());
				
				}else {
					// add product to unique list
					OrderProduct op = new OrderProduct();
					op.setProductId(orderProduct.getKey().getProductId());
					op.setProductQty(orderProduct.getProductQty());
					uniqueProducts.add(op);
				}				
			}
		}

		if (uniqueProducts.isEmpty()) {
			// no orders or products found for user
			return ResponseEntity.notFound().build();
		}
		
		// add all unique products to sortedProduct "heap"
		PriorityQueue<OrderProduct> sortedProducts = 
				new PriorityQueue<OrderProduct>(new PriorityQueue<>(new OrderProductComparator()));
		sortedProducts.addAll(uniqueProducts);
		
		List<ProductVector> returnVal = new ArrayList<ProductVector>();

		// Take the the top 3 products, and run a vector search for each, LIMIT of 4
		for (int productCount = 0; productCount < 3; productCount++) {
			// get top product from the heap
			OrderProduct topProduct = sortedProducts.poll();
			
			// get vector for this product
			Optional<ProductVectorsTableEntity> productWVector
					= dataApiServices.findProductVectorById(topProduct.getProductId());
			
			if (productWVector.isPresent()) {
				// run vector search with this product
				List<ProductVectorsTableEntity> likeProducts = 
						dataApiServices.findProductsByVector(
								productWVector.get().getProductVector(), 4);
				
				for (ProductVectorsTableEntity p : likeProducts) {
					// check for (and don't add) topProduct
					if (!p.getProductId().equals(topProduct.getProductId())) {
						// convert to ProductVector POJO
						ProductVector pv = mapProductVector(p);
						// add to return list
						returnVal.add(pv);
					}
				}
			}
		}

		return ResponseEntity.ok(returnVal);
	}
	
	private OrderProduct isProductInList(List<OrderProduct> productList, OrderEntity orderProduct) {
		
		for (OrderProduct product : productList) {
			if (product.getProductId().equals(orderProduct.getKey().getProductId())) {
				return product;
			}
		}
		return null;
	}
	
    private ProductVector mapProductVector(ProductVectorsTableEntity p) {
    	ProductVector pv = new ProductVector();
    	pv.setProductId(p.getProductId());
    	pv.setCategoryId(p.getCategoryId());
    	pv.setImages(p.getImages());
    	pv.setParentId(p.getParentId());
    	pv.setProductGroup(p.getGroup());
    	pv.setProductName(p.getName());
    	pv.setProductVector(p.getProductVector());
    	
    	return pv;
    }
}
