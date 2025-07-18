package com.datastax.tutorials.service.cartproducts;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.tutorials.service.dataapi.DataAPIServices;
import com.datastax.tutorials.service.dataapi.entities.CartProductTableEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Expose Rest Api to interact with cart product data. 
 *
 * @author Cedrick LUNVEN
 * @author Aaron PLOETZ 
 */
@RestController
@CrossOrigin(
  methods = {POST, GET, OPTIONS, PUT, DELETE, PATCH},
  maxAge = 3600,
  allowedHeaders = {"x-requested-with", "origin", "content-type", "accept"},
  origins = "*" 
)
@RequestMapping("/api/v1/carts/")
@Tag(name = "Cart Products Service", description="Provide crud operations for Cart Products")
public class CartProductsRestController {

	private DataAPIServices dataApiServices;
	
	public CartProductsRestController (DataAPIServices dataAPIRepo) {
		dataApiServices = dataAPIRepo;
	}
	
    /**
     * Retrieve all cart products for a cart.
     * @param req
     *      current request
     * @param cartid
     *      cart identifier (UUID)
     * @return
     *      list of products in the cart
     */
    @GetMapping("/{cartid}/")
    @Operation(
     summary = "Retrieve all products in a carts by a cartid",
     description= "Find **cart products list** for a cart by its id `SELECT * FROM cart_products WHERE cart_id =?`",
     responses = {
       @ApiResponse(
         responseCode = "200",
         description = "A list of products is provided for the cart",
         content = @Content(
           mediaType = "application/json",
           schema = @Schema(implementation = CartProduct.class, name = "CartProduct")
         )
       ),
       @ApiResponse(
         responseCode = "404", 
         description = "cartId not found",
         content = @Content(mediaType = "")),
       @ApiResponse(
         responseCode = "400",
         description = "Invalid parameter check cartId format."),
       @ApiResponse(
         responseCode = "500",
         description = "Internal error.") 
    })
    public ResponseEntity<Stream<CartProduct>> findAllByCartId(
            HttpServletRequest req, 
            @PathVariable(value = "cartid")
            @Parameter(name = "cartid", description = "cart identifier (UUID)", example = "5929e846-53e8-473e-8525-80b666c46a83")
            UUID cartid) {
    	List<CartProductTableEntity> results = dataApiServices.getCartProductByCartId(cartid);
    
    	return ResponseEntity.ok(results.stream().map(this::mapCartProduct));
    }
    
    /**
     * Add product to cart.
     * @param req
     *      current request
     * @param cartid
     *      cart identifier (UUID)
     * @param productid
     *      product identifier (UUID)
     * @param quantity
     *      quantity of product to add
     * @return
     *      list of products in the cart
     */
    @PutMapping("/{cartid}/products/{productid}/")
    @Operation(
     summary = "Add a product to a cart",
     description= "Add a product to a cart `INSERT INTO cart_products (cart_id,product_timestamp,product_id,product_description,product_name,quantity) VALUES (?,toTimestamp(now()),?,?,?,?);`",
     responses = {
       @ApiResponse(
         responseCode = "200",
         description = "Add a product to the cart, return list of products",
         content = @Content(
           mediaType = "application/json",
           schema = @Schema(implementation = CartProduct.class, name = "CartProduct")
         )
       ),
       @ApiResponse(
         responseCode = "404", 
         description = "cartId not found",
         content = @Content(mediaType = "")),
       @ApiResponse(
         responseCode = "400",
         description = "Invalid parameter check cartId format."),
       @ApiResponse(
         responseCode = "500",
         description = "Internal error.") 
    })
    public ResponseEntity<Stream<CartProduct>> addProductToCart(
            HttpServletRequest req, 
            @RequestBody CartProduct product,
            @PathVariable(value = "cartid")
            @Parameter(name = "cartid", description = "cart identifier (UUID)", example = "5929e846-53e8-473e-8525-80b666c46a83")
            UUID cartid,
            @PathVariable(value = "productid")
            @Parameter(name = "productid", description = "product identifier", example = "LSS123XL")
            String productid) {

    	// set keys
    	product.setCartId(cartid);
    	product.setProductId(productid);
    	//   set timestamp
    	product.setProductTimestamp(new Date());

    	// get current cart contents
    	List<CartProductTableEntity> cart = dataApiServices.getCartProductByCartId(cartid);
    	boolean found = false;
    	
    	// iterate through cart
    	for (CartProductTableEntity cpe : cart) {
    		
    		// check for productid
    		if (cpe.getProductId().equals(productid)) {
    			// FOUND
    			found = true;
    			// update quantity
    			cpe.setQuantity(cpe.getQuantity() + product.getQuantity());   

    			// save to DB
    			dataApiServices.saveCartProduct(cpe);
    			break;
    		}
    	}
    	
    	if (!found) {
    		// map CartProduct to entity
	    	CartProductTableEntity cpe = mapCartProductTableEntity(product);
	    	// save to DB
	    	dataApiServices.saveCartProduct(cpe);
	    	// add to in-memory cart
	    	cart.add(cpe);
    	}
    	
    	// return current cart contents    	
    	return ResponseEntity.ok(cart.stream().map(this::mapCartProduct));
    }
    
    /**
     * Delete product from cart.
     * @param req
     *      current request
     * @param cartid
     *      cart identifier (UUID)
     * @param productid
     *      product identifier (UUID)
     * @return
     *      list of products in the cart
     */
    @DeleteMapping("/{cartid}/products/{productid}")
    @Operation(
     summary = "Remove a product from a cart",
     description= "Remove a product from a cart `DELETE FROM cart_products WHERE cart_id=5929e846-53e8-473e-8525-80b666c46a83 AND product_timestamp='2022-01-20 10:47:12' AND productId='LSS123XL';",
     responses = {
       @ApiResponse(
         responseCode = "200",
         description = "Remove a product from the cart, return list of products",
         content = @Content(
           mediaType = "application/json",
           schema = @Schema(implementation = CartProduct.class, name = "CartProduct")
         )
       ),
       @ApiResponse(
         responseCode = "404", 
         description = "cartId or productId not found",
         content = @Content(mediaType = "")),
       @ApiResponse(
         responseCode = "400",
         description = "Invalid parameter check cartId or productId format."),
       @ApiResponse(
         responseCode = "500",
         description = "Internal error.") 
    })
    public ResponseEntity<Stream<CartProduct>> removeProductFromCart (
    		HttpServletRequest req,
            @PathVariable(value = "cartid")
            @Parameter(name = "cartid", description = "cart identifier (UUID)", example = "5929e846-53e8-473e-8525-80b666c46a83")
            UUID cartid,
            @PathVariable(value = "productid")
            @Parameter(name = "productid", description = "productid identifier", example = "LSS123XL")
            String productid) {
    	
    	// get current cart
    	List<CartProductTableEntity> cart = dataApiServices.getCartProductByCartId(cartid);
    	List<CartProductTableEntity> returnVal = new ArrayList<CartProductTableEntity>();
    	
    	// iterate through cart
    	for (CartProductTableEntity cpe : cart) {
    		
    		// check for productid
    		if (cpe.getProductId().equals(productid)) {
    			// delete
    			dataApiServices.deleteCartProduct(cpe.getCartId(), cpe.getProductId(), cpe.getProductTimestamp());
    			
    		} else {
    			returnVal.add(cpe);
    		}
    	}
    	
    	// map cart product list to entity list and return
    	return ResponseEntity.ok(returnVal.stream().map(this::mapCartProduct));
    }
    
    /**
     * Mapping Entity => POJO.
     *
     * @param p
     *      entity
     * @return
     *      POJO bean
     */
    private CartProduct mapCartProduct(CartProductTableEntity _cpe) {
        CartProduct cp = new CartProduct();
        cp.setCartId(_cpe.getCartId());
        cp.setProductTimestamp(Date.from(_cpe.getProductTimestamp()));
        cp.setProductId(_cpe.getProductId());
        cp.setProductDesc(_cpe.getProductDescription());
        cp.setProductName(_cpe.getProductName());
        cp.setQuantity(_cpe.getQuantity());
        return cp;
    }
    
    /**
     * Mapping POJO => Entity.
     *
     * @param _cp
     *      POJO
     * @return
     *      entity
     */
    private CartProductTableEntity mapCartProductTableEntity(CartProduct _cp) {
        CartProductTableEntity cpe = new CartProductTableEntity();
        
        cpe.setCartId(_cp.getCartId());
        cpe.setProductTimestamp(_cp.getProductTimestamp().toInstant());
        cpe.setProductId(_cp.getProductId());
        cpe.setProductDescription(_cp.getProductDesc());
        cpe.setProductName(_cp.getProductName());
        cpe.setQuantity(_cp.getQuantity());
        return cpe;
    }
}

