<!--- STARTEXCLUDE --->
## 🔥 Building an E-commerce Website 🔥

[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/datastaxdevs/workshop-ecommerce-app)
[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Discord](https://img.shields.io/discord/685554030159593522)](https://discord.com/widget?id=685554030159593522&theme=dark)

<img src="data/img/splash.png?raw=true" align="right" width="400px"/>

## Materials for the Session

It doesn't matter if you join our workshop live or you prefer to do at your own pace, we have you covered. In this repository, you'll find everything you need for this workshop:

- [Slide deck - week 1](./slides_wk1.pdf)
- [Slide deck - week 2](./slides_wk2.pdf)
- [Slide deck - week 3](./slides_wk3.pdf)
- [Slide deck - week 4](./slides_wk4.pdf)
- [Questions and Answers](https://community.datastax.com/)
- [Worskhop code] (https://github.com/datastaxdevs/workshop-ecommerce-app)

If you cannot attend this workshop live, recordings of this workshop and many more is available on [Youtube](https://youtube.com/datastaxdevs).

## Homework

<img src="data/img/build-an-ecommerce-app.png" width="200" align=right />

Complete the homework to earn the badge for this workshop (**awarded only at the end of the series**).

1. Implement Google login and take SCREENSHOT(s).
2. Don't worry about submitting it just yet.  We'll have instructions on how to do that at the end of the series!

## 📋 Table of contents

1. [Introduction](#1-introduction)
2. [Create your Database](#2-create-astra-db-instance)
3. [Create your schema](#3-create-your-schema)
4. [Populate the dataset](#4-populate-the-data)
5. [Create a token](#5-create-your-token)
6. [Setup your application](#6-setup-your-application)
7. [Enable Social Login](#7-enable-social-login)
8. [Run Unit Tests](#8-run-unit-tests)
9. [Start the Application](#9-start-the-application)

## 1. Introduction

Are you building or do you support an e-commerce website?  If so, then this content is for **you**!

Worldwide digital sales in 2020 eclipsed four trillion dollars (USD).  Businesses that want to compete, need a high performing e-commerce website.  Here, we will demonstrate how to build a high performing persistence layer with DataStax **`ASTRA DB`**.

Why does an e-commerce site need to be fast?  Because most consumers will leave a web page or a mobile app if it takes longer than a few seconds to load.  In the content below, we will cover how to build high-performing data models and services, helping you to build a e-commerce site with high throughput and low latency.

## 2. Create Astra DB and Streaming Instances

You can skip to step 2c if you have already created a keyspace `ecommerce` in database `demos`. Otherwise (if you did not attend the previous installment of the e-commerce worksop):

**`ASTRA DB`** is the simplest way to run Cassandra with zero operations - just push the button and get your cluster. No credit card required, $25.00 USD credit every month, roughly 20M read/write operations, 80GB storage monthly - sufficient to run small production workloads.

#### ✅ 2a. Register

If you do not have an account yet, register and sign in to Astra DB: This is FREE and NO CREDIT CARD is required. [https://astra.datastax.com](https://astra.dev/yt-11-30): You can use your `Github`, `Google` accounts or register with an `email`.

_Make sure to chose a password with minimum 8 characters, containing upper and lowercase letters, at least one number and special character_

#### ✅ 2b. Create a DB on the "FREE" plan

Follow this [guide](https://docs.datastax.com/en/astra/docs/creating-your-astra-database.html), to set up a pay as you go database with a free $25 monthly credit. You will find below recommended values to enter:

- **For the database name** - `demos`

- **For the keyspace name** - `ecommerce`

_You can technically use whatever name(s) you want and update the code to reflect the keyspace. This is really to get you on a happy path for the first run._

- **For provider and region**: For Astra DB, select GCP as a provider and then the related region is where your database will reside physically (choose one close to you or your users).

- **Create the database**. Review all the fields to make sure they are as shown, and click the `Create Database` button.

**👁️ Walkthrough**

*The Walkthrough mentions a different keyspace, make sure to use `ecommerce`*

![image](data/img/astra-create-db.gif?raw=true)
You will see your new database `pending` in the Dashboard.

![my-pic](data/img/db-pending.png?raw=true)

#### ✅ 2c. Ensure the database turns to active state

To connect to the database programmatically, you need to make sure the status will change to `Active`. This happens when the database is ready, and will only take 2-3 minutes. You will also receive an email when it is ready.

**👁️ Expected Output**

![my-pic](data/img/db-active.png?raw=true)

If it's in a `standby` state you can hit `Connect` and `CQL Console` on top.

You should see a message something like below.

**👁️ Expected Output**

```cql
{"message":"Resuming your database, please try again shortly."}
```

#### ✅ 2d. Create a Streaming Tenant and Topics on the "FREE" plan

Here we will walk through how to create an Astra Streaming Tenant.  Start by clicking the "Create Stream" button in the left navigation pane.

![image](data/img/create-stream.png?raw=true)

On the next page, provide a name for your tenant and select a provider/region.  Click the blue "Create Tenant" button when complete.

![image](data/img/create_streaming_tenant.png?raw=true)

Note that Tenant Names must be unique across providers.  To ensure uniqueness, name it "ecommerce-" followed by your name or company.

Now we need to create topics _within_ our tenant.  Click on the link or on the "Topics" tab.  You should see the "default" namespace with an "Add Topic" button (on the right).  Click the "Add Topic" button.

![image](data/img/add_topic1.png?raw=true)

Name the topic "pending-orders" and make sure that the "Persistent" switch is selected.  Don't worry about the "Partitioned" switch for now.  Click the "Add Topic" button when ready.

![image](data/img/add_topic2.png?raw=true)

Repeat this process to add 3 more topics:
 - picked-orders
 - shipped-orders
 - completed-orders

When you are done, your "Topics" tab should look similar to this:

![image](data/img/streaming_topics_final.png?raw=true)

[🏠 Back to Table of Contents](#-table-of-contents)

## 3. Create your schema

**Introduction**
This section will provide DDL to create three tables inside the "ecommerce" keyspace: category, price, and product.

#### Session 1 - Product data model ####
The `product` table supports all product data queries, and uses `product_id` as a single key.  It has a few columns for specific product data, but any ad-hoc or non-standard properties can be added to the `specifications` map.

The `category` table will support all product navigation service calls.  It is designed to provide recursive, hierarchical navigation without a pre-set limit on the number of levels.  The top-most level only exists as a `parent_id`, and the bottom-most level contains products.

The `price` table was intentionally split-off from product.  There are several reasons for this.  Price data is much more likely to change than pure product data (different read/write patterns).  Also, large enterprises typically have separate teams for product and price, meaning they will usually have different micro-service layers and data stores.

The `featured_product_groups` table was a late-add, to be able to provide some extra "atmosphere" of an e-commerce website.  This way, the UI has a means by which to highlight a few, select products.

#### Session 2 - Shopping Cart data model ####

The `user_carts` table supports cart metadata.  Carts are not expected to be long-lived, so they have a default TTL (time to live) of 60 days (5,184,000 seconds).  Carts also have a `name` as a part of the key, so that the user can have multiple carts (think "wish lists").

The `cart_products` table holds data on the products added to the cart.  The cart uses `product_timestamp` as the first clustering key in descending order; this way products in the cart will be listed with the most-recently-added products at the top.  Like `user_carts`, each entry has a 60 day TTL.

#### Session 3 - User Profile data model ####

The `user` table holds all data on the user, keyed by a single PRIMARY KEY on `user_id`.  It's main features contain TEXT (string) data for common user properties, as well as a collection of `addresses`.  This is because users (especially B-to-B) may have multiple addresses (mail-to, ship-to, bill-to, etc).  The `addresses` collection is built on a special user defined type (UDT) and `FROZEN` to treat the collection as a Binary Large OBject (BLOB) to reduce tombstones (required by CQL).

As mentioned above, the `address` UDT contains properties used for postal contacts.  All properties are of the TEXT datatype.

The `user_by_email` table is intended to be used as a "manual index" on email address. Essentially, it is a lookup table returning the `user_id` associated with an email address.  This is necessary as `user_email` is nigh-unique (in terms of cardinality of values), and thus a CQL secondary index would perform quite poorly.

#### Session 4 - Order Processing System data model ####

The `order_by_id` table holds detail on each order.  It partitions on `order_id` for optimal data distribution, and clusters on `product_name` and `product_id` for sort order.  The columns specific to the order itself (and not a product) are `STATIC` so that they are only stored once (with the partition key).

The `order_by_user` table holds a reference to each order by `user_id`.  The idea, is that this table is queried by `user_id` and the results are shown on an "order history" page for that user.  Then, each order can be clicked-on, revealing the detail contained in the `order_by_id` table.  `order_id` is a TimeUUID (version 1 UUID) type, which is converted into a human-readable timestamp in the service layer.

The `order_status_history` table maintains a history of each status for an order.  It is meant to be used with queries to the `order_by_id` table, so that a user may see the status progression of their order.

### ✅ 3a. Open the CqlConsole on Astra

```sql
use ecommerce;
```

### ✅ 3b. Execute the following CQL script to create the schema

```sql
/* Session 1 - Product data model */
/* category table */
CREATE TABLE IF NOT EXISTS category (
    parent_id UUID,
    category_id UUID,
    name TEXT,
    image TEXT,
    products LIST<TEXT>,
PRIMARY KEY (parent_id,category_id));

/* price table */
CREATE TABLE IF NOT EXISTS price (
    product_id TEXT,
    store_id TEXT,
    value DECIMAL,
PRIMARY KEY(product_id,store_id));

/* product table */
CREATE TABLE IF NOT EXISTS product (
    product_id TEXT,
    product_group TEXT,
    name TEXT,
    brand TEXT,
    model_number TEXT,
    short_desc TEXT,
    long_desc TEXT,
    specifications MAP<TEXT,TEXT>,
    linked_documents MAP<TEXT,TEXT>,
    images SET<TEXT>,
PRIMARY KEY(product_id));

/* featured product groups table */
CREATE TABLE IF NOT EXISTS featured_product_groups (
    feature_id INT,
    category_id UUID,
    name TEXT,
    image TEXT,
    parent_id UUID,
    price DECIMAL,
PRIMARY KEY (feature_id,category_id));

/* Session 2 - Shopping Cart data model */
CREATE TABLE IF NOT EXISTS user_carts (
    user_id uuid,
    cart_name text,
    cart_id uuid,
    cart_is_active boolean,
    user_email text,
    PRIMARY KEY (user_id, cart_name, cart_id)
) WITH default_time_to_live = 5184000;

CREATE TABLE IF NOT EXISTS cart_products (
    cart_id uuid,
    product_timestamp timestamp,
    product_id text,
    product_description text,
    product_name text,
    quantity int,
    PRIMARY KEY (cart_id, product_timestamp, product_id)
) WITH CLUSTERING ORDER BY (product_timestamp DESC, product_id ASC)
  AND default_time_to_live = 5184000;

/* Session 3 - User Profile data model */
CREATE TYPE IF NOT EXISTS address (
  type TEXT,
  mailto_name TEXT,
  street TEXT,
  street2 TEXT,
  city TEXT,
  state_province TEXT,
  postal_code TEXT,
  country TEXT
);

CREATE TABLE IF NOT EXISTS user (
  user_id UUID,
  user_email TEXT,
  picture_url TEXT,
  first_name TEXT,
  last_name TEXT,
  locale TEXT,
  addresses LIST<FROZEN<address>>,
  session_id TEXT,
  password TEXT,
  password_timestamp TIMESTAMP,
  PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS user_by_email (
  user_email TEXT PRIMARY KEY,
  user_id UUID
);

/* Session 4 - Order Processing System data model */
CREATE TABLE IF NOT EXISTS order_by_id (
    order_id timeuuid,
    product_name text,
    product_id text,
    order_shipping_handling decimal static,
    order_status text static,
    order_subtotal decimal static,
    order_tax decimal static,
    order_total decimal static,
    payment_method text static,
    product_price decimal,
    product_qty int,
    shipping_address address static,
    PRIMARY KEY (order_id, product_name, product_id)
) WITH CLUSTERING ORDER BY (product_name ASC, product_id ASC);

CREATE TABLE IF NOT EXISTS order_by_user (
    user_id uuid,
    order_id timeuuid,
    order_status text,
    order_total decimal,
    PRIMARY KEY (user_id, order_id)
) WITH CLUSTERING ORDER BY (order_id DESC);

CREATE TABLE IF NOT EXISTS order_status_history (
    order_id timeuuid,
    status_timestamp timestamp,
    order_status text,
    PRIMARY KEY (order_id, status_timestamp)
) WITH CLUSTERING ORDER BY (status_timestamp DESC);

/* Session 5 - Vector Search data */
CREATE TABLE product_vectors (
    product_id TEXT PRIMARY KEY,
    name TEXT,
    product_group TEXT,
    parent_id UUID,
    category_id UUID,
    images SET<TEXT>,
    product_vector vector<float,384>);

CREATE CUSTOM INDEX ON product_vectors(product_vector) USING 'StorageAttachedIndex';
```

[🏠 Back to Table of Contents](#-table-of-contents)

## 4. Populate the Data

#### ✅ 4a. Execute the following script to populate the tables with the data below

#### Session 1 - Product data ####

```sql
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Clothing',18105592-77aa-4469-8556-833b419dacf4,'ls534.png',ffdac25a-0244-4894-bb31-a0884bc82aa9);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Tech Accessories',5929e846-53e8-473e-8525-80b666c46a83,'',ffdac25a-0244-4894-bb31-a0884bc82aa9);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Cups and Mugs',675cf3a2-2752-4de7-ae2e-849471c29f51,'',ffdac25a-0244-4894-bb31-a0884bc82aa9);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Wall Decor',591bf485-de09-4b46-8fd2-5d9dc7ca101e,'bh001.png',ffdac25a-0244-4894-bb31-a0884bc82aa9);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('T-Shirts',91455473-212e-4c6e-8bec-1da06779ae10,'ls534.png',18105592-77aa-4469-8556-833b419dacf4);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Hoodies',6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1,'',18105592-77aa-4469-8556-833b419dacf4);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Jackets',d887b049-d16c-46e1-8c94-0a1280dedc30,'',18105592-77aa-4469-8556-833b419dacf4);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Mousepads',d04dfb5b-69c6-4e97-b572-e9e390165a84,'',5929e846-53e8-473e-8525-80b666c46a83);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Wrist Rests',aa161129-d456-45ba-b1f0-fac7898b6d06,'',5929e846-53e8-473e-8525-80b666c46a83);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Laptop Covers',1c4b8599-78df-4f93-9c52-578bd959a3a5,'',5929e846-53e8-473e-8525-80b666c46a83);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Cups',7536fdef-fcd9-44a3-9360-0bffd2904408,'',675cf3a2-2752-4de7-ae2e-849471c29f51);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Coffee Mugs',20374300-185c-4ee5-b0bc-77fbdc3a21ed,'',675cf3a2-2752-4de7-ae2e-849471c29f51);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Travel Mugs',0660483e-2fad-447b-b19a-63ab4935e482,'',675cf3a2-2752-4de7-ae2e-849471c29f51);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Posters',fdbe9dcb-6878-4216-a64d-27c094b1b075,'',591bf485-de09-4b46-8fd2-5d9dc7ca101e);
INSERT INTO category (name,category_id,image,parent_id) VALUES ('Wall Art',943482f9-070c-4390-bb30-2107b6fe653a,'bh001.png',591bf485-de09-4b46-8fd2-5d9dc7ca101e);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('Men''s "Go Away...Annotation" T-Shirt',99c4d825-d262-4a95-a04e-cc72e7e273c1,'ls534.png',91455473-212e-4c6e-8bec-1da06779ae10,['LS534S','LS534M','LS534L','LS534XL','LS5342XL','LS5343XL']);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('Men''s "Your Face...Autowired" T-Shirt',3fa13eee-d057-48d0-b0ae-2d83af9e3e3e,'ln355.png',91455473-212e-4c6e-8bec-1da06779ae10,['LN355S','LN355M','LN355L','LN355XL','LN3552XL','LN3553XL']);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('Bigheads',2f25a732-0744-406d-baee-3e8131cbe500,'bh001.png',943482f9-070c-4390-bb30-2107b6fe653a,['bh001','bh002','bh003']);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('DataStax Gray Track Jacket',f629e107-b219-4563-a852-6909fd246949,'dss821.jpg',d887b049-d16c-46e1-8c94-0a1280dedc30,['DSS821S','DSS821M','DSS821L','DSS821XL']);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('DataStax Vintage 2015 MVP Hoodie',86d234a4-6b97-476c-ada8-efb344d39743,'dsh915.jpg',6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1,['DSH915S','DSH915M','DSH915L','DSH915XL']);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('DataStax Black Hoodie',b9bed3c0-0a76-44ea-bce6-f5f21611a3f1,'dsh916.jpg',6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1,['DSH916S','DSH916M','DSH916L','DSH916XL']);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('Apache Cassandra 3.0 Contributor T-Shirt',95ae4613-0184-46ee-b4b0-adfe882754a8,'apc30a.jpg',91455473-212e-4c6e-8bec-1da06779ae10,['APC30S','APC30M','APC30L','APC30XL','APC302XL','APC303XL']);
INSERT INTO category (name,category_id,image,parent_id,products) VALUES ('DataStax Astra "One Team" Long Sleeve Tee',775be203-1a84-4822-9645-4da98ca2b2d8,'dsa1121.jpg',91455473-212e-4c6e-8bec-1da06779ae10,['DSA1121S','DSA1121M','DSA1121L','DSA1121XL','DSA11212XL','DSA11213XL']);

INSERT INTO price(product_id,store_id,value) VALUES ('LS534S','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LS534M','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LS534L','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LS534XL','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LS5342XL','web',16.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LS5343XL','web',16.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LN355S','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LN355M','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LN355L','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LN355XL','web',14.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LN3552XL','web',16.99);
INSERT INTO price(product_id,store_id,value) VALUES ('LN3553XL','web',16.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSA1121S','web',21.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSA1121M','web',21.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSA1121L','web',21.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSA1121XL','web',21.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSA11212XL','web',23.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSA11213XL','web',23.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSS821S','web',44.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSS821M','web',44.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSS821L','web',44.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSS821XL','web',44.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH915S','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH915M','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH915L','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH915XL','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH916S','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH916M','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH916L','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('DSH916XL','web',35.99);
INSERT INTO price(product_id,store_id,value) VALUES ('APC30S','web',15.99);
INSERT INTO price(product_id,store_id,value) VALUES ('APC30M','web',15.99);
INSERT INTO price(product_id,store_id,value) VALUES ('APC30L','web',15.99);
INSERT INTO price(product_id,store_id,value) VALUES ('APC30XL','web',15.99);
INSERT INTO price(product_id,store_id,value) VALUES ('APC302XL','web',17.99);
INSERT INTO price(product_id,store_id,value) VALUES ('APC303XL','web',17.99);

INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LS534S','LS534','Go Away Annotation T-Shirt','NerdShirts','NS101','Men''s Small "Go Away...Annotation" T-Shirt','Having to answer support questions when you really want to get back to coding?  Wear this to work, and let there be no question as to what you''d rather be doing.',{'size':'Small','material':'cotton, polyester','cut':'men''s','color':'black'},{'ls534.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LS534M','LS534','Go Away Annotation T-Shirt','NerdShirts','NS101','Men''s Medium "Go Away...Annotation" T-Shirt','Having to answer support questions when you really want to get back to coding?  Wear this to work, and let there be no question as to what you''d rather be doing.',{'size':'Medium','material':'cotton, polyester','cut':'men''s','color':'black'},{'ls534.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LS534L','LS534','Go Away Annotation T-Shirt','NerdShirts','NS101','Men''s Large "Go Away...Annotation" T-Shirt','Having to answer support questions when you really want to get back to coding?  Wear this to work, and let there be no question as to what you''d rather be doing.',{'size':'Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ls534.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LS534XL','LS534','Go Away Annotation T-Shirt','NerdShirts','NS101','Men''s Extra Large "Go Away...Annotation" T-Shirt','Having to answer support questions when you really want to get back to coding?  Wear this to work, and let there be no question as to what you''d rather be doing.',{'size':'Extra Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ls534.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LS5342XL','LS534','Go Away Annotation T-Shirt','NerdShirts','NS101','Men''s 2x Large "Go Away...Annotation" T-Shirt','Having to answer support questions when you really want to get back to coding?  Wear this to work, and let there be no question as to what you''d rather be doing.',{'size':'2x Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ls534.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LS5343XL','LS534','Go Away Annotation T-Shirt','NerdShirts','NS101','Men''s 3x Large "Go Away...Annotation" T-Shirt','Having to answer support questions when you really want to get back to coding?  Wear this to work, and let there be no question as to what you''d rather be doing.',{'size':'3x Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ls534.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LN355S','LN355','Your Face is an @Autowired @Bean T-Shirt','NerdShirts','NS102','Men''s Small "Your Face...Autowired" T-Shirt','Everyone knows that one person who overuses the "your face" jokes.',{'size':'Small','material':'cotton, polyester','cut':'men''s','color':'black'},{'ln355.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LN355M','LN355','Your Face is an @Autowired @Bean T-Shirt','NerdShirts','NS102','Men''s Medium "Your Face...Autowired" T-Shirt','Everyone knows that one person who overuses the "your face" jokes.',{'size':'Medium','material':'cotton, polyester','cut':'men''s','color':'black'},{'ln355.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LN355L','LN355','Your Face is an @Autowired @Bean T-Shirt','NerdShirts','NS102','Men''s Large "Your Face...Autowired" T-Shirt','Everyone knows that one person who overuses the "your face" jokes.',{'size':'Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ln355.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LN355XL','LN355','Your Face is an @Autowired @Bean T-Shirt','NerdShirts','NS102','Men''s Extra Large "Your Face...Autowired" T-Shirt','Everyone knows that one person who overuses the "your face" jokes.',{'size':'Extra Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ln355.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LN3552XL','LN355','Your Face is an @Autowired @Bean T-Shirt','NerdShirts','NS102','Men''s 2x Large "Your Face...Autowired" T-Shirt','Everyone knows that one person who overuses the "your face" jokes.',{'size':'2x Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ln355.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('LN355XL','LN355','Your Face is an @Autowired @Bean T-Shirt','NerdShirts','NS102','Men''s 3x Large "Your Face...Autowired" T-Shirt','Everyone knows that one person who overuses the "your face" jokes.',{'size':'3x Large','material':'cotton, polyester','cut':'men''s','color':'black'},{'ln355.png'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSA1121S','DSA1121','DataStax Astra "One Team" Long Sleeve Tee','DataStax','DSA1121','DataStax Astra "One Team" Long Sleeve Tee - Small','Given out at the internal summit, show how proud you are to talk about the world''s best multi-region, multi-cloud, serverless database!',{'size':'Small','material':'cotton, polyester','color':'black'},{'dsa1121.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSA1121M','DSA1121','DataStax Astra "One Team" Long Sleeve Tee','DataStax','DSA1121','DataStax Astra "One Team" Long Sleeve Tee - Medium','Given out at the internal summit, show how proud you are to talk about the world''s best multi-region, multi-cloud, serverless database!',{'size':'Medium','material':'cotton, polyester','color':'black'},{'dsa1121.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSA1121L','DSA1121','DataStax Astra "One Team" Long Sleeve Tee','DataStax','DSA1121','DataStax Astra "One Team" Long Sleeve Tee - Large','Given out at the internal summit, show how proud you are to talk about the world''s best multi-region, multi-cloud, serverless database!',{'size':'Large','material':'cotton, polyester','color':'black'},{'dsa1121.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSA1121XL','DSA1121','DataStax Astra "One Team" Long Sleeve Tee','DataStax','DSA1121','DataStax Astra "One Team" Long Sleeve Tee - Extra Large','Given out at the internal summit, show how proud you are to talk about the world''s best multi-region, multi-cloud, serverless database!',{'size':'Extra Large','material':'cotton, polyester','color':'black'},{'dsa1121.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSA11212XL','DSA1121','DataStax Astra "One Team" Long Sleeve Tee','DataStax','DSA1121','DataStax Astra "One Team" Long Sleeve Tee - 2X Large','Given out at the internal summit, show how proud you are to talk about the world''s best multi-region, multi-cloud, serverless database!',{'size':'2X Large','material':'cotton, polyester','color':'black'},{'dsa1121.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSA11213XL','DSA1121','DataStax Astra "One Team" Long Sleeve Tee','DataStax','DSA1121','DataStax Astra "One Team" Long Sleeve Tee - 3X Large','Given out at the internal summit, show how proud you are to talk about the world''s best multi-region, multi-cloud, serverless database!',{'size':'3X Large','material':'cotton, polyester','color':'black'},{'dsa1121.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('APC30S','APC30','Apache Cassandra 3.0 Contributor T-Shirt','Apache Foundation','APC30','Apache Cassandra 3.0 Contributor T-Shirt - Small','Own a piece of Cassandra history with this Apache Cassandra 3.0 "Contributor" shirt.  Given out to all of the contributors to the project in 2016, shows the unmistakable Cassandra Eye on the front, with the
engine rebuild" on the back.',{'size':'Small','material':'cotton, polyester','color':'black'},{'apc30.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('APC30M','APC30','Apache Cassandra 3.0 Contributor T-Shirt','Apache Foundation','APC30','Apache Cassandra 3.0 Contributor T-Shirt - Medium','Own a piece of Cassandra history with this Apache Cassandra 3.0 "Contributor" shirt.  Given out to all of the contributors to the project in 2016, shows the unmistakable Cassandra Eye on the front, with the
engine rebuild" on the back.',{'size':'Medium','material':'cotton, polyester','color':'black'},{'apc30.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('APC30L','APC30','Apache Cassandra 3.0 Contributor T-Shirt','Apache Foundation','APC30','Apache Cassandra 3.0 Contributor T-Shirt - Large','Own a piece of Cassandra history with this Apache Cassandra 3.0 "Contributor" shirt.  Given out to all of the contributors to the project in 2016, shows the unmistakable Cassandra Eye on the front, with the
engine rebuild" on the back.',{'size':'Large','material':'cotton, polyester','color':'black'},{'apc30.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('APC30XL','APC30','Apache Cassandra 3.0 Contributor T-Shirt','Apache Foundation','APC30','Apache Cassandra 3.0 Contributor T-Shirt - Extra Large','Own a piece of Cassandra history with this Apache Cassandra 3.0 "Contributor" shirt.  Given out to all of the contributors to the project in 2016, shows the unmistakable Cassandra Eye on the front, with the
engine rebuild" on the back.',{'size':'Extra Large','material':'cotton, polyester','color':'black'},{'apc30.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('APC302XL','APC30','Apache Cassandra 3.0 Contributor T-Shirt','Apache Foundation','APC30','Apache Cassandra 3.0 Contributor T-Shirt - 2X Large','Own a piece of Cassandra history with this Apache Cassandra 3.0 "Contributor" shirt.  Given out to all of the contributors to the project in 2016, shows the unmistakable Cassandra Eye on the front, with the
engine rebuild" on the back.',{'size':'2X Large','material':'cotton, polyester','color':'black'},{'apc30.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('APC303XL','APC30','Apache Cassandra 3.0 Contributor T-Shirt','Apache Foundation','APC30','Apache Cassandra 3.0 Contributor T-Shirt - 3X Large','Own a piece of Cassandra history with this Apache Cassandra 3.0 "Contributor" shirt.  Given out to all of the contributors to the project in 2016, shows the unmistakable Cassandra Eye on the front, with the
engine rebuild" on the back.',{'size':'3X Large','material':'cotton, polyester','color':'black'},{'apc30.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSS821S','DSS821','DataStax Gray Track Jacket','DataStax','DSS821','DataStax Gray Track Jacket - Small','This lightweight polyester jacket will be your favorite while hiking the trails or teeing off.',{'size':'Small','material':'polyester','color':'gray'},{'dss821.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSS821M','DSS821','DataStax Gray Track Jacket','DataStax','DSS821','DataStax Gray Track Jacket - Medium','This lightweight polyester jacket will be your favorite while hiking the trails or teeing off.',{'size':'Medium','material':'polyester','color':'gray'},{'dss821.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSS821L','DSS821','DataStax Gray Track Jacket','DataStax','DSS821','DataStax Gray Track Jacket - Large','This lightweight polyester jacket will be your favorite while hiking the trails or teeing off.',{'size':'Large','material':'polyester','color':'gray'},{'dss821.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSS821XL','DSS821','DataStax Gray Track Jacket','DataStax','DSS821','DataStax Gray Track Jacket - Extra Large','This lightweight polyester jacket will be your favorite while hiking the trails or teeing off.',{'size':'Extra Large','material':'polyester','color':'gray'},{'dss821.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH915S','DSH915','DataStax Vintage 2015 MVP Hoodie','DataStax','DSS915','DataStax Vintage 2015 MVP Hoodie - Small','Given out to MVPs at the 2015 DataStax Cassandra Summit.  Warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Small','color':'black'},{'dsh915.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH915M','DSH915','DataStax Vintage 2015 MVP Hoodie','DataStax','DSS915','DataStax Vintage 2015 MVP Hoodie - Medium','Given out to MVPs at the 2015 DataStax Cassandra Summit.  Warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Medium','color':'black'},{'dsh915.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH915L','DSH915','DataStax Vintage 2015 MVP Hoodie','DataStax','DSS915','DataStax Vintage 2015 MVP Hoodie - Large','Given out to MVPs at the 2015 DataStax Cassandra Summit.  Warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Large','color':'black'},{'dsh915.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH915XL','DSH915','DataStax Vintage 2015 MVP Hoodie','DataStax','DSS915','DataStax Vintage 2015 MVP Hoodie - Extra Large','Given out to MVPs at the 2015 DataStax Cassandra Summit.  Warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Extra Large','color':'black'},{'dsh915.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH916S','DSH916','DataStax Black Hoodie','DataStax','DSS916','DataStax Black Hoodie - Small','Super warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Small','color':'black'},{'dsh916.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH916M','DSH916','DataStax Black Hoodie','DataStax','DSS916','DataStax Black Hoodie - Medium','Super warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Medium','color':'black'},{'dsh916.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH916L','DSH916','DataStax Black Hoodie','DataStax','DSS916','DataStax Black Hoodie - Large','Super warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Large','color':'black'},{'dsh916.jpg'});
INSERT INTO product(product_id,product_group,name,brand,model_number,short_desc,long_desc,specifications,images)
VALUES ('DSH916XL','DSH916','DataStax Black Hoodie','DataStax','DSS916','DataStax Black Hoodie - Extra Large','Super warm!  You will underestimate how many times you will fall asleep wearing this!',{'size':'Extra Large','color':'black'},{'dsh916.jpg'});

INSERT INTO featured_product_groups (feature_id,name,category_id,image,price,parent_id) VALUES (202112,'DataStax Gray Track Jacket',f629e107-b219-4563-a852-6909fd246949,'dss821.jpg',44.99,d887b049-d16c-46e1-8c94-0a1280dedc30);
INSERT INTO featured_product_groups (feature_id,name,category_id,image,price,parent_id) VALUES (202112,'DataStax Black Hoodie',b9bed3c0-0a76-44ea-bce6-f5f21611a3f1,'dsh916.jpg',35.99,6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1);
INSERT INTO featured_product_groups (feature_id,name,category_id,image,price,parent_id) VALUES (202112,'Apache Cassandra 3.0 Contributor T-Shirt',95ae4613-0184-46ee-b4b0-adfe882754a8,'apc30a.jpg',15.99,91455473-212e-4c6e-8bec-1da06779ae10);
INSERT INTO featured_product_groups (feature_id,name,category_id,image,price,parent_id) VALUES (202112,'DataStax Astra "One Team" Long Sleeve Tee',775be203-1a84-4822-9645-4da98ca2b2d8,'dsa1121.jpg',21.99,91455473-212e-4c6e-8bec-1da06779ae10);

```

Although it's not advised to use wildcards as below, you can verify the data has been created with the following command.

```
select * from CATEGORY;
```

**Notes:**

 - The "top" categories of the product hierarchy can be retrieved using a `parent_id` of "ffdac25a-0244-4894-bb31-a0884bc82aa9".
 - Without specifying a `category_id`, all categories for the `parent_id` are returned.
 - When a category from the "bottom" of the hierarchy is queried, a populated `products` ArrayList will be returned.  From there, the returned `product_id`s can be used with the `/product` service.
 - Category navigation is achieved by using the `parent_id` and `category_id` properties returned for each category (to build the "next level" category links).
 - `/category/ffdac25a-0244-4894-bb31-a0884bc82aa9`  =>  Category[Clothing, Cups and Mugs, Tech Accessories, Wall Decor]
 - `/category/ffdac25a-0244-4894-bb31-a0884bc82aa9/18105592-77aa-4469-8556-833b419dacf4`  =>  Category[Clothing]
 - `/category/18105592-77aa-4469-8556-833b419dacf4`  =>  Category[T-Shirts, Hoodies, Jackets]
 - `/category/91455473-212e-4c6e-8bec-1da06779ae10`  =>  Category[Men's "Your Face...Autowired" T-Shirt, Men's "Go Away...Annotation" T-Shirt]
 - The featured products table is a simple way for web marketers to promote small numbers of products, and have them appear in an organized fashion on the main page.  The `feature_id` key is simply an integer, with the default being `202112` (for December, 2021).  You can (of course) use other numeric naming schemes.

#### Session 5 - Vector Search data ####

```sql
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('APC302XL','Apache Cassandra 3.0 Contributor T-Shirt','APC30',{'apc30.jpg'},[-0.017914, 0.047001, -0.043849, 0.032079, -0.005304, -0.021054, -0.051062, -0.036407, -0.025608, 0.030632, 0.040037, -0.112481, 0.098847, -0.03305, -0.029418, 0.008438, 0.052184, 0.008258, 0.026073, -0.056667, -0.148579, 0.023316, 0.084169, 0.034493, 0.022939, -0.053468, -0.07508, 0.001588, 0.025848, -0.011609, -0.065647, 0.034109, -0.008731, 0.048096, -0.02324, 0.028126, 0.072643, 0.0639, -0.049096, 0.000709, 0.067148, -0.134303, -0.064587, -0.012289, -0.043439, -0.033283, -0.06031, 0.055121, -0.050286, 0.000523, 0.013903, 0.007861, 0.064052, -0.051382, -0.035406, 0.015252, 0.023036, 0.030494, 0.052689, 0.009772, 0.060328, -0.035902, -0.066696, 0.008155, 0.064813, -0.024585, 0.043381, -0.005954, -0.017688, -0.001508, 0.033911, -0.018174, 0.02517, 0.022316, 0.065634, 0.135518, 0.034431, -0.043242, -0.032092, 0.009342, -0.040611, 0.057521, -0.000504, 0.049166, -0.08864, 0.095823, -0.077706, -0.043835, -0.015939, -0.060531, 0.00845, -0.000427, 0.060204, -0.074245, -0.106467, 0.061783, -0.019643, 0.012084, -0.028223, 0.073623, -0.084297, -0.015715, -0.033686, 0.011969, 0.001039, -0.044592, -0.02553, 0.013294, 0.001545, 0.002015, 0.041811, -0.033286, -0.102022, -0.080178, -0.030388, 0.011046, 0.034265, -0.031686, 0.009146, 0.004248, 0.046729, -0.012935, -0.048127, -0.017077, 0.056968, -0.005556, 0.029968, -1.1894e-33, 0.047996, 0.120196, 0.079322, 0.015611, 0.091496, -0.045049, -0.008182, -0.090856, -0.06025, -0.07356, 0.045048, 0.046531, 0.033841, 0.039501, -0.042125, 0.033299, -0.030491, -0.041392, -0.053736, -0.056381, 0.029233, -0.035194, -0.011031, 0.090384, 0.020802, 0.030104, 0.060822, -0.003859, 0.032389, 0.014347, 0.019102, 0.046754, 0.068188, 0.008545, -0.046038, 0.005304, -0.021208, 0.008992, -0.00824, -0.026239, 0.012361, 0.061586, 0.066628, -0.039666, -0.042849, 0.007753, 0.095943, -0.021174, 0.041022, 0.040602, 0.04532, 0.005434, 0.04619, 0.020761, 0.010343, -0.047209, 0.050648, 0.011074, 0.04116, -0.044051, -0.032299, -0.007062, -0.00544, 0.01171, -0.00055, 0.013431, -0.075822, -0.004492, 0.008728, -0.036666, 0.015555, 0.101941, 0.044371, 0.025311, -0.077316, -0.01664, -0.055106, 0.015232, -0.014826, 0.049888, 0.003705, -0.078957, -0.030546, -0.022782, 0.008368, -0.045034, 0.001986, 0.013604, 0.013148, -0.031426, 0.153068, 0.083573, 0.064333, -0.018486, -0.048573, 2.801e-34, 0.011236, 0.04032, 0.045948, 0.03184, 0.111179, -0.072356, -0.088575, 0.058405, -0.002492, 0.022988, 0.056481, -0.021095, -0.056353, -0.059324, 0.003082, 0.055867, 0.059454, 0.023976, -0.080879, -0.019192, -0.039299, -0.001861, -0.004059, -0.012523, 0.040241, -0.057695, 0.070312, 0.048392, -0.066452, -0.039659, -0.068954, -0.006298, -0.030643, -0.005777, 0.072749, -0.037262, -0.03958, 0.064192, 0.016196, -0.010096, 0.004286, 0.007184, 0.021136, 0.043952, -0.01875, 0.011678, 0.01173, -0.067317, -0.049263, 0.000939, 0.027929, -0.064393, 0.128587, 0.051264, 0.043536, 0.085619, 0.023756, 0.113316, 0.071343, -0.007564, 0.03438, -0.001625, -0.074928, 0.016605, 0.014762, -0.013138, -0.032724, 0.030116, -0.086654, 0.025713, -0.019029, -0.042361, -0.018026, -0.066879, 0.045015, -0.098726, -0.029978, 0.076111, -0.072215, 0.002693, -0.073575, -0.008396, -0.023593, 0.027149, 0.152263, -0.00437, -0.000413, 0.05063, -0.040523, 0.090834, -0.046994, 0.052096, -0.054825, 0.066779, 0.094774, -1.7029e-08, 0.018177, -0.068119, -0.124672, 0.01091, 0.114977, 0.062214, 0.040134, 0.005636, -0.011734, 0.146007, -0.014915, -0.022865, -0.02121, -0.020202, 0.052273, -0.098392, -0.007143, -0.019954, -0.127744, -0.108948, -0.033263, -0.0015, 0.045024, 0.000749, -0.012211, 0.079039, -0.036305, 0.036727, -0.011361, -0.050916, -0.077569, -0.00801, -0.046507, -0.068498, -0.036037, 0.036982, -0.03157, 0.01302, 0.0355, 0.080411, -0.039664, 0.034658, -0.03509, 0.05666, 0.014906, -0.012224, -0.055645, 0.00597, 0.006323, -0.0523, -0.018763, -0.116111, 0.042749, -0.002138, -0.042451, -0.022335, 0.041963, 0.045725, 0.096362, 0.034946, 0.043689, -0.065951, -0.039344, 0.002223], 91455473-212e-4c6e-8bec-1da06779ae10, 95ae4613-0184-46ee-b4b0-adfe882754a8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('APC303XL','Apache Cassandra 3.0 Contributor T-Shirt','APC30',{'apc30.jpg'},[-0.017914, 0.047001, -0.043849, 0.032079, -0.005304, -0.021054, -0.051062, -0.036407, -0.025608, 0.030632, 0.040037, -0.112481, 0.098847, -0.03305, -0.029418, 0.008438, 0.052184, 0.008258, 0.026073, -0.056667, -0.148579, 0.023316, 0.084169, 0.034493, 0.022939, -0.053468, -0.07508, 0.001588, 0.025848, -0.011609, -0.065647, 0.034109, -0.008731, 0.048096, -0.02324, 0.028126, 0.072643, 0.0639, -0.049096, 0.000709, 0.067148, -0.134303, -0.064587, -0.012289, -0.043439, -0.033283, -0.06031, 0.055121, -0.050286, 0.000523, 0.013903, 0.007861, 0.064052, -0.051382, -0.035406, 0.015252, 0.023036, 0.030494, 0.052689, 0.009772, 0.060328, -0.035902, -0.066696, 0.008155, 0.064813, -0.024585, 0.043381, -0.005954, -0.017688, -0.001508, 0.033911, -0.018174, 0.02517, 0.022316, 0.065634, 0.135518, 0.034431, -0.043242, -0.032092, 0.009342, -0.040611, 0.057521, -0.000504, 0.049166, -0.08864, 0.095823, -0.077706, -0.043835, -0.015939, -0.060531, 0.00845, -0.000427, 0.060204, -0.074245, -0.106467, 0.061783, -0.019643, 0.012084, -0.028223, 0.073623, -0.084297, -0.015715, -0.033686, 0.011969, 0.001039, -0.044592, -0.02553, 0.013294, 0.001545, 0.002015, 0.041811, -0.033286, -0.102022, -0.080178, -0.030388, 0.011046, 0.034265, -0.031686, 0.009146, 0.004248, 0.046729, -0.012935, -0.048127, -0.017077, 0.056968, -0.005556, 0.029968, -1.1894e-33, 0.047996, 0.120196, 0.079322, 0.015611, 0.091496, -0.045049, -0.008182, -0.090856, -0.06025, -0.07356, 0.045048, 0.046531, 0.033841, 0.039501, -0.042125, 0.033299, -0.030491, -0.041392, -0.053736, -0.056381, 0.029233, -0.035194, -0.011031, 0.090384, 0.020802, 0.030104, 0.060822, -0.003859, 0.032389, 0.014347, 0.019102, 0.046754, 0.068188, 0.008545, -0.046038, 0.005304, -0.021208, 0.008992, -0.00824, -0.026239, 0.012361, 0.061586, 0.066628, -0.039666, -0.042849, 0.007753, 0.095943, -0.021174, 0.041022, 0.040602, 0.04532, 0.005434, 0.04619, 0.020761, 0.010343, -0.047209, 0.050648, 0.011074, 0.04116, -0.044051, -0.032299, -0.007062, -0.00544, 0.01171, -0.00055, 0.013431, -0.075822, -0.004492, 0.008728, -0.036666, 0.015555, 0.101941, 0.044371, 0.025311, -0.077316, -0.01664, -0.055106, 0.015232, -0.014826, 0.049888, 0.003705, -0.078957, -0.030546, -0.022782, 0.008368, -0.045034, 0.001986, 0.013604, 0.013148, -0.031426, 0.153068, 0.083573, 0.064333, -0.018486, -0.048573, 2.801e-34, 0.011236, 0.04032, 0.045948, 0.03184, 0.111179, -0.072356, -0.088575, 0.058405, -0.002492, 0.022988, 0.056481, -0.021095, -0.056353, -0.059324, 0.003082, 0.055867, 0.059454, 0.023976, -0.080879, -0.019192, -0.039299, -0.001861, -0.004059, -0.012523, 0.040241, -0.057695, 0.070312, 0.048392, -0.066452, -0.039659, -0.068954, -0.006298, -0.030643, -0.005777, 0.072749, -0.037262, -0.03958, 0.064192, 0.016196, -0.010096, 0.004286, 0.007184, 0.021136, 0.043952, -0.01875, 0.011678, 0.01173, -0.067317, -0.049263, 0.000939, 0.027929, -0.064393, 0.128587, 0.051264, 0.043536, 0.085619, 0.023756, 0.113316, 0.071343, -0.007564, 0.03438, -0.001625, -0.074928, 0.016605, 0.014762, -0.013138, -0.032724, 0.030116, -0.086654, 0.025713, -0.019029, -0.042361, -0.018026, -0.066879, 0.045015, -0.098726, -0.029978, 0.076111, -0.072215, 0.002693, -0.073575, -0.008396, -0.023593, 0.027149, 0.152263, -0.00437, -0.000413, 0.05063, -0.040523, 0.090834, -0.046994, 0.052096, -0.054825, 0.066779, 0.094774, -1.7029e-08, 0.018177, -0.068119, -0.124672, 0.01091, 0.114977, 0.062214, 0.040134, 0.005636, -0.011734, 0.146007, -0.014915, -0.022865, -0.02121, -0.020202, 0.052273, -0.098392, -0.007143, -0.019954, -0.127744, -0.108948, -0.033263, -0.0015, 0.045024, 0.000749, -0.012211, 0.079039, -0.036305, 0.036727, -0.011361, -0.050916, -0.077569, -0.00801, -0.046507, -0.068498, -0.036037, 0.036982, -0.03157, 0.01302, 0.0355, 0.080411, -0.039664, 0.034658, -0.03509, 0.05666, 0.014906, -0.012224, -0.055645, 0.00597, 0.006323, -0.0523, -0.018763, -0.116111, 0.042749, -0.002138, -0.042451, -0.022335, 0.041963, 0.045725, 0.096362, 0.034946, 0.043689, -0.065951, -0.039344, 0.002223], 91455473-212e-4c6e-8bec-1da06779ae10, 95ae4613-0184-46ee-b4b0-adfe882754a8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('APC30L','Apache Cassandra 3.0 Contributor T-Shirt','APC30',{'apc30.jpg'},[-0.017914, 0.047001, -0.043849, 0.032079, -0.005304, -0.021054, -0.051062, -0.036407, -0.025608, 0.030632, 0.040037, -0.112481, 0.098847, -0.03305, -0.029418, 0.008438, 0.052184, 0.008258, 0.026073, -0.056667, -0.148579, 0.023316, 0.084169, 0.034493, 0.022939, -0.053468, -0.07508, 0.001588, 0.025848, -0.011609, -0.065647, 0.034109, -0.008731, 0.048096, -0.02324, 0.028126, 0.072643, 0.0639, -0.049096, 0.000709, 0.067148, -0.134303, -0.064587, -0.012289, -0.043439, -0.033283, -0.06031, 0.055121, -0.050286, 0.000523, 0.013903, 0.007861, 0.064052, -0.051382, -0.035406, 0.015252, 0.023036, 0.030494, 0.052689, 0.009772, 0.060328, -0.035902, -0.066696, 0.008155, 0.064813, -0.024585, 0.043381, -0.005954, -0.017688, -0.001508, 0.033911, -0.018174, 0.02517, 0.022316, 0.065634, 0.135518, 0.034431, -0.043242, -0.032092, 0.009342, -0.040611, 0.057521, -0.000504, 0.049166, -0.08864, 0.095823, -0.077706, -0.043835, -0.015939, -0.060531, 0.00845, -0.000427, 0.060204, -0.074245, -0.106467, 0.061783, -0.019643, 0.012084, -0.028223, 0.073623, -0.084297, -0.015715, -0.033686, 0.011969, 0.001039, -0.044592, -0.02553, 0.013294, 0.001545, 0.002015, 0.041811, -0.033286, -0.102022, -0.080178, -0.030388, 0.011046, 0.034265, -0.031686, 0.009146, 0.004248, 0.046729, -0.012935, -0.048127, -0.017077, 0.056968, -0.005556, 0.029968, -1.1894e-33, 0.047996, 0.120196, 0.079322, 0.015611, 0.091496, -0.045049, -0.008182, -0.090856, -0.06025, -0.07356, 0.045048, 0.046531, 0.033841, 0.039501, -0.042125, 0.033299, -0.030491, -0.041392, -0.053736, -0.056381, 0.029233, -0.035194, -0.011031, 0.090384, 0.020802, 0.030104, 0.060822, -0.003859, 0.032389, 0.014347, 0.019102, 0.046754, 0.068188, 0.008545, -0.046038, 0.005304, -0.021208, 0.008992, -0.00824, -0.026239, 0.012361, 0.061586, 0.066628, -0.039666, -0.042849, 0.007753, 0.095943, -0.021174, 0.041022, 0.040602, 0.04532, 0.005434, 0.04619, 0.020761, 0.010343, -0.047209, 0.050648, 0.011074, 0.04116, -0.044051, -0.032299, -0.007062, -0.00544, 0.01171, -0.00055, 0.013431, -0.075822, -0.004492, 0.008728, -0.036666, 0.015555, 0.101941, 0.044371, 0.025311, -0.077316, -0.01664, -0.055106, 0.015232, -0.014826, 0.049888, 0.003705, -0.078957, -0.030546, -0.022782, 0.008368, -0.045034, 0.001986, 0.013604, 0.013148, -0.031426, 0.153068, 0.083573, 0.064333, -0.018486, -0.048573, 2.801e-34, 0.011236, 0.04032, 0.045948, 0.03184, 0.111179, -0.072356, -0.088575, 0.058405, -0.002492, 0.022988, 0.056481, -0.021095, -0.056353, -0.059324, 0.003082, 0.055867, 0.059454, 0.023976, -0.080879, -0.019192, -0.039299, -0.001861, -0.004059, -0.012523, 0.040241, -0.057695, 0.070312, 0.048392, -0.066452, -0.039659, -0.068954, -0.006298, -0.030643, -0.005777, 0.072749, -0.037262, -0.03958, 0.064192, 0.016196, -0.010096, 0.004286, 0.007184, 0.021136, 0.043952, -0.01875, 0.011678, 0.01173, -0.067317, -0.049263, 0.000939, 0.027929, -0.064393, 0.128587, 0.051264, 0.043536, 0.085619, 0.023756, 0.113316, 0.071343, -0.007564, 0.03438, -0.001625, -0.074928, 0.016605, 0.014762, -0.013138, -0.032724, 0.030116, -0.086654, 0.025713, -0.019029, -0.042361, -0.018026, -0.066879, 0.045015, -0.098726, -0.029978, 0.076111, -0.072215, 0.002693, -0.073575, -0.008396, -0.023593, 0.027149, 0.152263, -0.00437, -0.000413, 0.05063, -0.040523, 0.090834, -0.046994, 0.052096, -0.054825, 0.066779, 0.094774, -1.7029e-08, 0.018177, -0.068119, -0.124672, 0.01091, 0.114977, 0.062214, 0.040134, 0.005636, -0.011734, 0.146007, -0.014915, -0.022865, -0.02121, -0.020202, 0.052273, -0.098392, -0.007143, -0.019954, -0.127744, -0.108948, -0.033263, -0.0015, 0.045024, 0.000749, -0.012211, 0.079039, -0.036305, 0.036727, -0.011361, -0.050916, -0.077569, -0.00801, -0.046507, -0.068498, -0.036037, 0.036982, -0.03157, 0.01302, 0.0355, 0.080411, -0.039664, 0.034658, -0.03509, 0.05666, 0.014906, -0.012224, -0.055645, 0.00597, 0.006323, -0.0523, -0.018763, -0.116111, 0.042749, -0.002138, -0.042451, -0.022335, 0.041963, 0.045725, 0.096362, 0.034946, 0.043689, -0.065951, -0.039344, 0.002223], 91455473-212e-4c6e-8bec-1da06779ae10, 95ae4613-0184-46ee-b4b0-adfe882754a8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('APC30M','Apache Cassandra 3.0 Contributor T-Shirt','APC30',{'apc30.jpg'},[-0.017914, 0.047001, -0.043849, 0.032079, -0.005304, -0.021054, -0.051062, -0.036407, -0.025608, 0.030632, 0.040037, -0.112481, 0.098847, -0.03305, -0.029418, 0.008438, 0.052184, 0.008258, 0.026073, -0.056667, -0.148579, 0.023316, 0.084169, 0.034493, 0.022939, -0.053468, -0.07508, 0.001588, 0.025848, -0.011609, -0.065647, 0.034109, -0.008731, 0.048096, -0.02324, 0.028126, 0.072643, 0.0639, -0.049096, 0.000709, 0.067148, -0.134303, -0.064587, -0.012289, -0.043439, -0.033283, -0.06031, 0.055121, -0.050286, 0.000523, 0.013903, 0.007861, 0.064052, -0.051382, -0.035406, 0.015252, 0.023036, 0.030494, 0.052689, 0.009772, 0.060328, -0.035902, -0.066696, 0.008155, 0.064813, -0.024585, 0.043381, -0.005954, -0.017688, -0.001508, 0.033911, -0.018174, 0.02517, 0.022316, 0.065634, 0.135518, 0.034431, -0.043242, -0.032092, 0.009342, -0.040611, 0.057521, -0.000504, 0.049166, -0.08864, 0.095823, -0.077706, -0.043835, -0.015939, -0.060531, 0.00845, -0.000427, 0.060204, -0.074245, -0.106467, 0.061783, -0.019643, 0.012084, -0.028223, 0.073623, -0.084297, -0.015715, -0.033686, 0.011969, 0.001039, -0.044592, -0.02553, 0.013294, 0.001545, 0.002015, 0.041811, -0.033286, -0.102022, -0.080178, -0.030388, 0.011046, 0.034265, -0.031686, 0.009146, 0.004248, 0.046729, -0.012935, -0.048127, -0.017077, 0.056968, -0.005556, 0.029968, -1.1894e-33, 0.047996, 0.120196, 0.079322, 0.015611, 0.091496, -0.045049, -0.008182, -0.090856, -0.06025, -0.07356, 0.045048, 0.046531, 0.033841, 0.039501, -0.042125, 0.033299, -0.030491, -0.041392, -0.053736, -0.056381, 0.029233, -0.035194, -0.011031, 0.090384, 0.020802, 0.030104, 0.060822, -0.003859, 0.032389, 0.014347, 0.019102, 0.046754, 0.068188, 0.008545, -0.046038, 0.005304, -0.021208, 0.008992, -0.00824, -0.026239, 0.012361, 0.061586, 0.066628, -0.039666, -0.042849, 0.007753, 0.095943, -0.021174, 0.041022, 0.040602, 0.04532, 0.005434, 0.04619, 0.020761, 0.010343, -0.047209, 0.050648, 0.011074, 0.04116, -0.044051, -0.032299, -0.007062, -0.00544, 0.01171, -0.00055, 0.013431, -0.075822, -0.004492, 0.008728, -0.036666, 0.015555, 0.101941, 0.044371, 0.025311, -0.077316, -0.01664, -0.055106, 0.015232, -0.014826, 0.049888, 0.003705, -0.078957, -0.030546, -0.022782, 0.008368, -0.045034, 0.001986, 0.013604, 0.013148, -0.031426, 0.153068, 0.083573, 0.064333, -0.018486, -0.048573, 2.801e-34, 0.011236, 0.04032, 0.045948, 0.03184, 0.111179, -0.072356, -0.088575, 0.058405, -0.002492, 0.022988, 0.056481, -0.021095, -0.056353, -0.059324, 0.003082, 0.055867, 0.059454, 0.023976, -0.080879, -0.019192, -0.039299, -0.001861, -0.004059, -0.012523, 0.040241, -0.057695, 0.070312, 0.048392, -0.066452, -0.039659, -0.068954, -0.006298, -0.030643, -0.005777, 0.072749, -0.037262, -0.03958, 0.064192, 0.016196, -0.010096, 0.004286, 0.007184, 0.021136, 0.043952, -0.01875, 0.011678, 0.01173, -0.067317, -0.049263, 0.000939, 0.027929, -0.064393, 0.128587, 0.051264, 0.043536, 0.085619, 0.023756, 0.113316, 0.071343, -0.007564, 0.03438, -0.001625, -0.074928, 0.016605, 0.014762, -0.013138, -0.032724, 0.030116, -0.086654, 0.025713, -0.019029, -0.042361, -0.018026, -0.066879, 0.045015, -0.098726, -0.029978, 0.076111, -0.072215, 0.002693, -0.073575, -0.008396, -0.023593, 0.027149, 0.152263, -0.00437, -0.000413, 0.05063, -0.040523, 0.090834, -0.046994, 0.052096, -0.054825, 0.066779, 0.094774, -1.7029e-08, 0.018177, -0.068119, -0.124672, 0.01091, 0.114977, 0.062214, 0.040134, 0.005636, -0.011734, 0.146007, -0.014915, -0.022865, -0.02121, -0.020202, 0.052273, -0.098392, -0.007143, -0.019954, -0.127744, -0.108948, -0.033263, -0.0015, 0.045024, 0.000749, -0.012211, 0.079039, -0.036305, 0.036727, -0.011361, -0.050916, -0.077569, -0.00801, -0.046507, -0.068498, -0.036037, 0.036982, -0.03157, 0.01302, 0.0355, 0.080411, -0.039664, 0.034658, -0.03509, 0.05666, 0.014906, -0.012224, -0.055645, 0.00597, 0.006323, -0.0523, -0.018763, -0.116111, 0.042749, -0.002138, -0.042451, -0.022335, 0.041963, 0.045725, 0.096362, 0.034946, 0.043689, -0.065951, -0.039344, 0.002223], 91455473-212e-4c6e-8bec-1da06779ae10, 95ae4613-0184-46ee-b4b0-adfe882754a8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('APC30S','Apache Cassandra 3.0 Contributor T-Shirt','APC30',{'apc30.jpg'},[-0.017914, 0.047001, -0.043849, 0.032079, -0.005304, -0.021054, -0.051062, -0.036407, -0.025608, 0.030632, 0.040037, -0.112481, 0.098847, -0.03305, -0.029418, 0.008438, 0.052184, 0.008258, 0.026073, -0.056667, -0.148579, 0.023316, 0.084169, 0.034493, 0.022939, -0.053468, -0.07508, 0.001588, 0.025848, -0.011609, -0.065647, 0.034109, -0.008731, 0.048096, -0.02324, 0.028126, 0.072643, 0.0639, -0.049096, 0.000709, 0.067148, -0.134303, -0.064587, -0.012289, -0.043439, -0.033283, -0.06031, 0.055121, -0.050286, 0.000523, 0.013903, 0.007861, 0.064052, -0.051382, -0.035406, 0.015252, 0.023036, 0.030494, 0.052689, 0.009772, 0.060328, -0.035902, -0.066696, 0.008155, 0.064813, -0.024585, 0.043381, -0.005954, -0.017688, -0.001508, 0.033911, -0.018174, 0.02517, 0.022316, 0.065634, 0.135518, 0.034431, -0.043242, -0.032092, 0.009342, -0.040611, 0.057521, -0.000504, 0.049166, -0.08864, 0.095823, -0.077706, -0.043835, -0.015939, -0.060531, 0.00845, -0.000427, 0.060204, -0.074245, -0.106467, 0.061783, -0.019643, 0.012084, -0.028223, 0.073623, -0.084297, -0.015715, -0.033686, 0.011969, 0.001039, -0.044592, -0.02553, 0.013294, 0.001545, 0.002015, 0.041811, -0.033286, -0.102022, -0.080178, -0.030388, 0.011046, 0.034265, -0.031686, 0.009146, 0.004248, 0.046729, -0.012935, -0.048127, -0.017077, 0.056968, -0.005556, 0.029968, -1.1894e-33, 0.047996, 0.120196, 0.079322, 0.015611, 0.091496, -0.045049, -0.008182, -0.090856, -0.06025, -0.07356, 0.045048, 0.046531, 0.033841, 0.039501, -0.042125, 0.033299, -0.030491, -0.041392, -0.053736, -0.056381, 0.029233, -0.035194, -0.011031, 0.090384, 0.020802, 0.030104, 0.060822, -0.003859, 0.032389, 0.014347, 0.019102, 0.046754, 0.068188, 0.008545, -0.046038, 0.005304, -0.021208, 0.008992, -0.00824, -0.026239, 0.012361, 0.061586, 0.066628, -0.039666, -0.042849, 0.007753, 0.095943, -0.021174, 0.041022, 0.040602, 0.04532, 0.005434, 0.04619, 0.020761, 0.010343, -0.047209, 0.050648, 0.011074, 0.04116, -0.044051, -0.032299, -0.007062, -0.00544, 0.01171, -0.00055, 0.013431, -0.075822, -0.004492, 0.008728, -0.036666, 0.015555, 0.101941, 0.044371, 0.025311, -0.077316, -0.01664, -0.055106, 0.015232, -0.014826, 0.049888, 0.003705, -0.078957, -0.030546, -0.022782, 0.008368, -0.045034, 0.001986, 0.013604, 0.013148, -0.031426, 0.153068, 0.083573, 0.064333, -0.018486, -0.048573, 2.801e-34, 0.011236, 0.04032, 0.045948, 0.03184, 0.111179, -0.072356, -0.088575, 0.058405, -0.002492, 0.022988, 0.056481, -0.021095, -0.056353, -0.059324, 0.003082, 0.055867, 0.059454, 0.023976, -0.080879, -0.019192, -0.039299, -0.001861, -0.004059, -0.012523, 0.040241, -0.057695, 0.070312, 0.048392, -0.066452, -0.039659, -0.068954, -0.006298, -0.030643, -0.005777, 0.072749, -0.037262, -0.03958, 0.064192, 0.016196, -0.010096, 0.004286, 0.007184, 0.021136, 0.043952, -0.01875, 0.011678, 0.01173, -0.067317, -0.049263, 0.000939, 0.027929, -0.064393, 0.128587, 0.051264, 0.043536, 0.085619, 0.023756, 0.113316, 0.071343, -0.007564, 0.03438, -0.001625, -0.074928, 0.016605, 0.014762, -0.013138, -0.032724, 0.030116, -0.086654, 0.025713, -0.019029, -0.042361, -0.018026, -0.066879, 0.045015, -0.098726, -0.029978, 0.076111, -0.072215, 0.002693, -0.073575, -0.008396, -0.023593, 0.027149, 0.152263, -0.00437, -0.000413, 0.05063, -0.040523, 0.090834, -0.046994, 0.052096, -0.054825, 0.066779, 0.094774, -1.7029e-08, 0.018177, -0.068119, -0.124672, 0.01091, 0.114977, 0.062214, 0.040134, 0.005636, -0.011734, 0.146007, -0.014915, -0.022865, -0.02121, -0.020202, 0.052273, -0.098392, -0.007143, -0.019954, -0.127744, -0.108948, -0.033263, -0.0015, 0.045024, 0.000749, -0.012211, 0.079039, -0.036305, 0.036727, -0.011361, -0.050916, -0.077569, -0.00801, -0.046507, -0.068498, -0.036037, 0.036982, -0.03157, 0.01302, 0.0355, 0.080411, -0.039664, 0.034658, -0.03509, 0.05666, 0.014906, -0.012224, -0.055645, 0.00597, 0.006323, -0.0523, -0.018763, -0.116111, 0.042749, -0.002138, -0.042451, -0.022335, 0.041963, 0.045725, 0.096362, 0.034946, 0.043689, -0.065951, -0.039344, 0.002223], 91455473-212e-4c6e-8bec-1da06779ae10, 95ae4613-0184-46ee-b4b0-adfe882754a8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('APC30XL','Apache Cassandra 3.0 Contributor T-Shirt','APC30',{'apc30.jpg'},[-0.017914, 0.047001, -0.043849, 0.032079, -0.005304, -0.021054, -0.051062, -0.036407, -0.025608, 0.030632, 0.040037, -0.112481, 0.098847, -0.03305, -0.029418, 0.008438, 0.052184, 0.008258, 0.026073, -0.056667, -0.148579, 0.023316, 0.084169, 0.034493, 0.022939, -0.053468, -0.07508, 0.001588, 0.025848, -0.011609, -0.065647, 0.034109, -0.008731, 0.048096, -0.02324, 0.028126, 0.072643, 0.0639, -0.049096, 0.000709, 0.067148, -0.134303, -0.064587, -0.012289, -0.043439, -0.033283, -0.06031, 0.055121, -0.050286, 0.000523, 0.013903, 0.007861, 0.064052, -0.051382, -0.035406, 0.015252, 0.023036, 0.030494, 0.052689, 0.009772, 0.060328, -0.035902, -0.066696, 0.008155, 0.064813, -0.024585, 0.043381, -0.005954, -0.017688, -0.001508, 0.033911, -0.018174, 0.02517, 0.022316, 0.065634, 0.135518, 0.034431, -0.043242, -0.032092, 0.009342, -0.040611, 0.057521, -0.000504, 0.049166, -0.08864, 0.095823, -0.077706, -0.043835, -0.015939, -0.060531, 0.00845, -0.000427, 0.060204, -0.074245, -0.106467, 0.061783, -0.019643, 0.012084, -0.028223, 0.073623, -0.084297, -0.015715, -0.033686, 0.011969, 0.001039, -0.044592, -0.02553, 0.013294, 0.001545, 0.002015, 0.041811, -0.033286, -0.102022, -0.080178, -0.030388, 0.011046, 0.034265, -0.031686, 0.009146, 0.004248, 0.046729, -0.012935, -0.048127, -0.017077, 0.056968, -0.005556, 0.029968, -1.1894e-33, 0.047996, 0.120196, 0.079322, 0.015611, 0.091496, -0.045049, -0.008182, -0.090856, -0.06025, -0.07356, 0.045048, 0.046531, 0.033841, 0.039501, -0.042125, 0.033299, -0.030491, -0.041392, -0.053736, -0.056381, 0.029233, -0.035194, -0.011031, 0.090384, 0.020802, 0.030104, 0.060822, -0.003859, 0.032389, 0.014347, 0.019102, 0.046754, 0.068188, 0.008545, -0.046038, 0.005304, -0.021208, 0.008992, -0.00824, -0.026239, 0.012361, 0.061586, 0.066628, -0.039666, -0.042849, 0.007753, 0.095943, -0.021174, 0.041022, 0.040602, 0.04532, 0.005434, 0.04619, 0.020761, 0.010343, -0.047209, 0.050648, 0.011074, 0.04116, -0.044051, -0.032299, -0.007062, -0.00544, 0.01171, -0.00055, 0.013431, -0.075822, -0.004492, 0.008728, -0.036666, 0.015555, 0.101941, 0.044371, 0.025311, -0.077316, -0.01664, -0.055106, 0.015232, -0.014826, 0.049888, 0.003705, -0.078957, -0.030546, -0.022782, 0.008368, -0.045034, 0.001986, 0.013604, 0.013148, -0.031426, 0.153068, 0.083573, 0.064333, -0.018486, -0.048573, 2.801e-34, 0.011236, 0.04032, 0.045948, 0.03184, 0.111179, -0.072356, -0.088575, 0.058405, -0.002492, 0.022988, 0.056481, -0.021095, -0.056353, -0.059324, 0.003082, 0.055867, 0.059454, 0.023976, -0.080879, -0.019192, -0.039299, -0.001861, -0.004059, -0.012523, 0.040241, -0.057695, 0.070312, 0.048392, -0.066452, -0.039659, -0.068954, -0.006298, -0.030643, -0.005777, 0.072749, -0.037262, -0.03958, 0.064192, 0.016196, -0.010096, 0.004286, 0.007184, 0.021136, 0.043952, -0.01875, 0.011678, 0.01173, -0.067317, -0.049263, 0.000939, 0.027929, -0.064393, 0.128587, 0.051264, 0.043536, 0.085619, 0.023756, 0.113316, 0.071343, -0.007564, 0.03438, -0.001625, -0.074928, 0.016605, 0.014762, -0.013138, -0.032724, 0.030116, -0.086654, 0.025713, -0.019029, -0.042361, -0.018026, -0.066879, 0.045015, -0.098726, -0.029978, 0.076111, -0.072215, 0.002693, -0.073575, -0.008396, -0.023593, 0.027149, 0.152263, -0.00437, -0.000413, 0.05063, -0.040523, 0.090834, -0.046994, 0.052096, -0.054825, 0.066779, 0.094774, -1.7029e-08, 0.018177, -0.068119, -0.124672, 0.01091, 0.114977, 0.062214, 0.040134, 0.005636, -0.011734, 0.146007, -0.014915, -0.022865, -0.02121, -0.020202, 0.052273, -0.098392, -0.007143, -0.019954, -0.127744, -0.108948, -0.033263, -0.0015, 0.045024, 0.000749, -0.012211, 0.079039, -0.036305, 0.036727, -0.011361, -0.050916, -0.077569, -0.00801, -0.046507, -0.068498, -0.036037, 0.036982, -0.03157, 0.01302, 0.0355, 0.080411, -0.039664, 0.034658, -0.03509, 0.05666, 0.014906, -0.012224, -0.055645, 0.00597, 0.006323, -0.0523, -0.018763, -0.116111, 0.042749, -0.002138, -0.042451, -0.022335, 0.041963, 0.045725, 0.096362, 0.034946, 0.043689, -0.065951, -0.039344, 0.002223], 91455473-212e-4c6e-8bec-1da06779ae10, 95ae4613-0184-46ee-b4b0-adfe882754a8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSA11212XL','DataStax Astra "One Team" Long Sleeve Tee','DSA1121',{'dsa1121.jpg'},[-0.128281, 0.09116, -0.05124, 0.041872, -0.010697, -0.042401, 0.013542, 0.028344, -0.031758, -0.048159, -0.014381, -0.021522, 0.022061, 0.001091, -0.004459, -0.01765, 0.00127, -0.054974, 0.003806, -0.057383, -0.030689, -0.069808, -0.009856, 0.045913, 0.033188, -0.003201, -0.030323, 0.013019, -0.02478, -0.134776, -0.031757, -0.025324, 0.045739, 0.122666, -0.020512, -0.046719, 0.050697, 0.074318, -0.071908, 0.049594, 0.05296, -0.071829, -0.026518, 0.021275, -0.044688, -0.004517, -0.119697, 0.031503, 0.063931, 0.132113, 0.02005, -0.029622, 0.035483, 0.078433, 0.002937, -0.009169, -0.033202, 0.014157, -0.00373, -0.021947, 0.098319, -0.039983, -0.062478, -0.003983, 0.053708, -0.02556, -0.04923, 0.133057, -0.036825, 0.037583, 0.050818, -0.045284, -0.053976, 0.072054, 0.050476, 0.112095, -0.006048, -0.006784, 0.021989, 0.045748, -0.052298, -0.002314, -0.037799, 0.069851, -0.052631, 0.101433, -0.038325, 0.018644, -0.058988, -0.048843, -0.029274, 0.079315, 0.066755, 0.019755, -0.012715, 0.06089, 0.021766, 0.017447, 0.015995, 0.018998, -0.013208, 0.03305, 0.012428, 0.01505, -0.04093, -0.132044, -0.035264, -0.033479, 0.05946, -0.070578, 0.000514, -0.019079, -0.047179, -0.054796, -0.100971, -0.020525, -0.134538, 0.053062, -0.023043, -0.020602, 0.013214, -0.006455, -0.104387, 0.031187, 0.079732, 0.007989, 0.024518, 1.3867e-33, -0.013965, 0.041949, 0.008109, 0.037002, 0.072333, -0.016775, -0.011283, -0.107011, -0.09913, 0.05747, -0.055104, 0.014418, 0.007826, 0.035289, 0.014108, -0.084076, 0.012282, 0.000176, -0.084882, 0.011905, -0.024416, -0.012512, 0.033489, -0.048052, -0.01924, 0.020635, -0.111069, 0.00745, 0.024936, 0.026581, -0.04044, 0.026642, -0.007218, 0.052131, -0.027928, -0.069486, -0.036498, -0.014839, 0.003706, 0.072703, 0.016124, 0.014113, -0.030583, -0.020418, -0.070632, -0.051854, 0.029575, -0.024864, 0.049117, -0.053699, 0.017122, 0.02788, -0.012669, -0.028201, 0.052266, -0.046625, 0.027294, 0.032979, 0.049887, 0.030718, -0.000316, 0.057238, 0.024089, 0.026383, -0.03658, 0.070992, 0.074688, -0.062611, 0.065399, -0.040395, 0.005701, 0.05166, 0.043032, 0.019983, 0.050351, 0.006587, 0.031543, 0.07645, -0.025486, -0.031392, -0.063568, -0.083924, -0.007298, 0.053036, 0.015939, -0.00838, 0.038113, -0.043831, -0.061393, 0.037627, -0.090122, -0.032869, -0.011807, -0.059326, 0.04968, -1.1305e-33, 0.123922, -0.022712, 0.052025, -0.027985, 0.11518, 0.02592, 0.101905, 0.091631, 0.024921, 0.095583, 0.109876, 0.021475, -0.102456, -0.007819, 0.041207, 0.096848, 0.093759, -0.036566, -0.027443, -0.050233, 0.036787, -0.063421, 0.02154, 0.103895, -0.006626, -0.00821, 0.065437, -0.054094, -0.031597, 0.001336, -0.0365, -0.012909, -0.011106, -0.007283, -0.005315, -0.004812, 0.052243, 0.119074, 0.036399, 0.000399, 0.007163, -0.009979, -0.055986, -0.002838, 0.042961, -0.077106, -0.080484, 0.00149, -0.030185, 0.040207, 0.011261, 0.006202, 0.050261, 0.005019, -0.007507, -0.00055, 0.019844, 0.014315, -0.101323, 0.013933, -0.003997, -0.010421, 0.010707, -0.000652, 0.054788, 0.053754, -0.011018, -0.083601, -0.103247, 0.078212, -0.058208, -0.021775, -0.034813, 0.041962, -0.003749, -0.079046, -0.081001, 0.064309, 0.030283, 0.087728, -0.094547, -0.028552, 0.010006, 0.025366, 0.18484, 0.028594, 0.015627, 0.128716, -0.008193, 0.058475, -0.008502, -0.053599, 0.00819, 0.059493, 0.056984, -1.7242e-08, -0.029973, -0.01065, 0.009777, -0.024941, 0.055131, 0.035772, -0.054124, -0.045651, 0.062665, -0.005865, 0.045888, -0.010536, 0.034085, -0.034141, -0.045801, -0.029558, -0.048121, -0.008161, -0.031021, -0.066639, -0.0052, 0.04086, -0.023477, -0.003341, -0.007351, 0.011513, -0.041883, 0.078883, 0.004945, -0.070312, 0.026609, -0.015324, 0.011051, -0.040054, 0.097843, -0.039656, 0.066084, -0.01111, 0.034593, 0.066441, -0.008927, 0.001803, -0.026357, 0.009546, 0.037642, 0.018216, -0.019941, -0.021066, -0.080779, -0.01716, 0.067168, -0.055346, -0.038624, 0.042253, 0.017768, -0.013819, -0.045199, -0.009714, -0.035598, 0.044979, -0.013873, -0.043237, 0.018578, 0.023844], 91455473-212e-4c6e-8bec-1da06779ae10, 775be203-1a84-4822-9645-4da98ca2b2d8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSA11213XL','DataStax Astra "One Team" Long Sleeve Tee','DSA1121',{'dsa1121.jpg'},[-0.128281, 0.09116, -0.05124, 0.041872, -0.010697, -0.042401, 0.013542, 0.028344, -0.031758, -0.048159, -0.014381, -0.021522, 0.022061, 0.001091, -0.004459, -0.01765, 0.00127, -0.054974, 0.003806, -0.057383, -0.030689, -0.069808, -0.009856, 0.045913, 0.033188, -0.003201, -0.030323, 0.013019, -0.02478, -0.134776, -0.031757, -0.025324, 0.045739, 0.122666, -0.020512, -0.046719, 0.050697, 0.074318, -0.071908, 0.049594, 0.05296, -0.071829, -0.026518, 0.021275, -0.044688, -0.004517, -0.119697, 0.031503, 0.063931, 0.132113, 0.02005, -0.029622, 0.035483, 0.078433, 0.002937, -0.009169, -0.033202, 0.014157, -0.00373, -0.021947, 0.098319, -0.039983, -0.062478, -0.003983, 0.053708, -0.02556, -0.04923, 0.133057, -0.036825, 0.037583, 0.050818, -0.045284, -0.053976, 0.072054, 0.050476, 0.112095, -0.006048, -0.006784, 0.021989, 0.045748, -0.052298, -0.002314, -0.037799, 0.069851, -0.052631, 0.101433, -0.038325, 0.018644, -0.058988, -0.048843, -0.029274, 0.079315, 0.066755, 0.019755, -0.012715, 0.06089, 0.021766, 0.017447, 0.015995, 0.018998, -0.013208, 0.03305, 0.012428, 0.01505, -0.04093, -0.132044, -0.035264, -0.033479, 0.05946, -0.070578, 0.000514, -0.019079, -0.047179, -0.054796, -0.100971, -0.020525, -0.134538, 0.053062, -0.023043, -0.020602, 0.013214, -0.006455, -0.104387, 0.031187, 0.079732, 0.007989, 0.024518, 1.3867e-33, -0.013965, 0.041949, 0.008109, 0.037002, 0.072333, -0.016775, -0.011283, -0.107011, -0.09913, 0.05747, -0.055104, 0.014418, 0.007826, 0.035289, 0.014108, -0.084076, 0.012282, 0.000176, -0.084882, 0.011905, -0.024416, -0.012512, 0.033489, -0.048052, -0.01924, 0.020635, -0.111069, 0.00745, 0.024936, 0.026581, -0.04044, 0.026642, -0.007218, 0.052131, -0.027928, -0.069486, -0.036498, -0.014839, 0.003706, 0.072703, 0.016124, 0.014113, -0.030583, -0.020418, -0.070632, -0.051854, 0.029575, -0.024864, 0.049117, -0.053699, 0.017122, 0.02788, -0.012669, -0.028201, 0.052266, -0.046625, 0.027294, 0.032979, 0.049887, 0.030718, -0.000316, 0.057238, 0.024089, 0.026383, -0.03658, 0.070992, 0.074688, -0.062611, 0.065399, -0.040395, 0.005701, 0.05166, 0.043032, 0.019983, 0.050351, 0.006587, 0.031543, 0.07645, -0.025486, -0.031392, -0.063568, -0.083924, -0.007298, 0.053036, 0.015939, -0.00838, 0.038113, -0.043831, -0.061393, 0.037627, -0.090122, -0.032869, -0.011807, -0.059326, 0.04968, -1.1305e-33, 0.123922, -0.022712, 0.052025, -0.027985, 0.11518, 0.02592, 0.101905, 0.091631, 0.024921, 0.095583, 0.109876, 0.021475, -0.102456, -0.007819, 0.041207, 0.096848, 0.093759, -0.036566, -0.027443, -0.050233, 0.036787, -0.063421, 0.02154, 0.103895, -0.006626, -0.00821, 0.065437, -0.054094, -0.031597, 0.001336, -0.0365, -0.012909, -0.011106, -0.007283, -0.005315, -0.004812, 0.052243, 0.119074, 0.036399, 0.000399, 0.007163, -0.009979, -0.055986, -0.002838, 0.042961, -0.077106, -0.080484, 0.00149, -0.030185, 0.040207, 0.011261, 0.006202, 0.050261, 0.005019, -0.007507, -0.00055, 0.019844, 0.014315, -0.101323, 0.013933, -0.003997, -0.010421, 0.010707, -0.000652, 0.054788, 0.053754, -0.011018, -0.083601, -0.103247, 0.078212, -0.058208, -0.021775, -0.034813, 0.041962, -0.003749, -0.079046, -0.081001, 0.064309, 0.030283, 0.087728, -0.094547, -0.028552, 0.010006, 0.025366, 0.18484, 0.028594, 0.015627, 0.128716, -0.008193, 0.058475, -0.008502, -0.053599, 0.00819, 0.059493, 0.056984, -1.7242e-08, -0.029973, -0.01065, 0.009777, -0.024941, 0.055131, 0.035772, -0.054124, -0.045651, 0.062665, -0.005865, 0.045888, -0.010536, 0.034085, -0.034141, -0.045801, -0.029558, -0.048121, -0.008161, -0.031021, -0.066639, -0.0052, 0.04086, -0.023477, -0.003341, -0.007351, 0.011513, -0.041883, 0.078883, 0.004945, -0.070312, 0.026609, -0.015324, 0.011051, -0.040054, 0.097843, -0.039656, 0.066084, -0.01111, 0.034593, 0.066441, -0.008927, 0.001803, -0.026357, 0.009546, 0.037642, 0.018216, -0.019941, -0.021066, -0.080779, -0.01716, 0.067168, -0.055346, -0.038624, 0.042253, 0.017768, -0.013819, -0.045199, -0.009714, -0.035598, 0.044979, -0.013873, -0.043237, 0.018578, 0.023844], 91455473-212e-4c6e-8bec-1da06779ae10, 775be203-1a84-4822-9645-4da98ca2b2d8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSA1121L','DataStax Astra "One Team" Long Sleeve Tee','DSA1121',{'dsa1121.jpg'},[-0.128281, 0.09116, -0.05124, 0.041872, -0.010697, -0.042401, 0.013542, 0.028344, -0.031758, -0.048159, -0.014381, -0.021522, 0.022061, 0.001091, -0.004459, -0.01765, 0.00127, -0.054974, 0.003806, -0.057383, -0.030689, -0.069808, -0.009856, 0.045913, 0.033188, -0.003201, -0.030323, 0.013019, -0.02478, -0.134776, -0.031757, -0.025324, 0.045739, 0.122666, -0.020512, -0.046719, 0.050697, 0.074318, -0.071908, 0.049594, 0.05296, -0.071829, -0.026518, 0.021275, -0.044688, -0.004517, -0.119697, 0.031503, 0.063931, 0.132113, 0.02005, -0.029622, 0.035483, 0.078433, 0.002937, -0.009169, -0.033202, 0.014157, -0.00373, -0.021947, 0.098319, -0.039983, -0.062478, -0.003983, 0.053708, -0.02556, -0.04923, 0.133057, -0.036825, 0.037583, 0.050818, -0.045284, -0.053976, 0.072054, 0.050476, 0.112095, -0.006048, -0.006784, 0.021989, 0.045748, -0.052298, -0.002314, -0.037799, 0.069851, -0.052631, 0.101433, -0.038325, 0.018644, -0.058988, -0.048843, -0.029274, 0.079315, 0.066755, 0.019755, -0.012715, 0.06089, 0.021766, 0.017447, 0.015995, 0.018998, -0.013208, 0.03305, 0.012428, 0.01505, -0.04093, -0.132044, -0.035264, -0.033479, 0.05946, -0.070578, 0.000514, -0.019079, -0.047179, -0.054796, -0.100971, -0.020525, -0.134538, 0.053062, -0.023043, -0.020602, 0.013214, -0.006455, -0.104387, 0.031187, 0.079732, 0.007989, 0.024518, 1.3867e-33, -0.013965, 0.041949, 0.008109, 0.037002, 0.072333, -0.016775, -0.011283, -0.107011, -0.09913, 0.05747, -0.055104, 0.014418, 0.007826, 0.035289, 0.014108, -0.084076, 0.012282, 0.000176, -0.084882, 0.011905, -0.024416, -0.012512, 0.033489, -0.048052, -0.01924, 0.020635, -0.111069, 0.00745, 0.024936, 0.026581, -0.04044, 0.026642, -0.007218, 0.052131, -0.027928, -0.069486, -0.036498, -0.014839, 0.003706, 0.072703, 0.016124, 0.014113, -0.030583, -0.020418, -0.070632, -0.051854, 0.029575, -0.024864, 0.049117, -0.053699, 0.017122, 0.02788, -0.012669, -0.028201, 0.052266, -0.046625, 0.027294, 0.032979, 0.049887, 0.030718, -0.000316, 0.057238, 0.024089, 0.026383, -0.03658, 0.070992, 0.074688, -0.062611, 0.065399, -0.040395, 0.005701, 0.05166, 0.043032, 0.019983, 0.050351, 0.006587, 0.031543, 0.07645, -0.025486, -0.031392, -0.063568, -0.083924, -0.007298, 0.053036, 0.015939, -0.00838, 0.038113, -0.043831, -0.061393, 0.037627, -0.090122, -0.032869, -0.011807, -0.059326, 0.04968, -1.1305e-33, 0.123922, -0.022712, 0.052025, -0.027985, 0.11518, 0.02592, 0.101905, 0.091631, 0.024921, 0.095583, 0.109876, 0.021475, -0.102456, -0.007819, 0.041207, 0.096848, 0.093759, -0.036566, -0.027443, -0.050233, 0.036787, -0.063421, 0.02154, 0.103895, -0.006626, -0.00821, 0.065437, -0.054094, -0.031597, 0.001336, -0.0365, -0.012909, -0.011106, -0.007283, -0.005315, -0.004812, 0.052243, 0.119074, 0.036399, 0.000399, 0.007163, -0.009979, -0.055986, -0.002838, 0.042961, -0.077106, -0.080484, 0.00149, -0.030185, 0.040207, 0.011261, 0.006202, 0.050261, 0.005019, -0.007507, -0.00055, 0.019844, 0.014315, -0.101323, 0.013933, -0.003997, -0.010421, 0.010707, -0.000652, 0.054788, 0.053754, -0.011018, -0.083601, -0.103247, 0.078212, -0.058208, -0.021775, -0.034813, 0.041962, -0.003749, -0.079046, -0.081001, 0.064309, 0.030283, 0.087728, -0.094547, -0.028552, 0.010006, 0.025366, 0.18484, 0.028594, 0.015627, 0.128716, -0.008193, 0.058475, -0.008502, -0.053599, 0.00819, 0.059493, 0.056984, -1.7242e-08, -0.029973, -0.01065, 0.009777, -0.024941, 0.055131, 0.035772, -0.054124, -0.045651, 0.062665, -0.005865, 0.045888, -0.010536, 0.034085, -0.034141, -0.045801, -0.029558, -0.048121, -0.008161, -0.031021, -0.066639, -0.0052, 0.04086, -0.023477, -0.003341, -0.007351, 0.011513, -0.041883, 0.078883, 0.004945, -0.070312, 0.026609, -0.015324, 0.011051, -0.040054, 0.097843, -0.039656, 0.066084, -0.01111, 0.034593, 0.066441, -0.008927, 0.001803, -0.026357, 0.009546, 0.037642, 0.018216, -0.019941, -0.021066, -0.080779, -0.01716, 0.067168, -0.055346, -0.038624, 0.042253, 0.017768, -0.013819, -0.045199, -0.009714, -0.035598, 0.044979, -0.013873, -0.043237, 0.018578, 0.023844], 91455473-212e-4c6e-8bec-1da06779ae10, 775be203-1a84-4822-9645-4da98ca2b2d8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSA1121M','DataStax Astra "One Team" Long Sleeve Tee','DSA1121',{'dsa1121.jpg'},[-0.128281, 0.09116, -0.05124, 0.041872, -0.010697, -0.042401, 0.013542, 0.028344, -0.031758, -0.048159, -0.014381, -0.021522, 0.022061, 0.001091, -0.004459, -0.01765, 0.00127, -0.054974, 0.003806, -0.057383, -0.030689, -0.069808, -0.009856, 0.045913, 0.033188, -0.003201, -0.030323, 0.013019, -0.02478, -0.134776, -0.031757, -0.025324, 0.045739, 0.122666, -0.020512, -0.046719, 0.050697, 0.074318, -0.071908, 0.049594, 0.05296, -0.071829, -0.026518, 0.021275, -0.044688, -0.004517, -0.119697, 0.031503, 0.063931, 0.132113, 0.02005, -0.029622, 0.035483, 0.078433, 0.002937, -0.009169, -0.033202, 0.014157, -0.00373, -0.021947, 0.098319, -0.039983, -0.062478, -0.003983, 0.053708, -0.02556, -0.04923, 0.133057, -0.036825, 0.037583, 0.050818, -0.045284, -0.053976, 0.072054, 0.050476, 0.112095, -0.006048, -0.006784, 0.021989, 0.045748, -0.052298, -0.002314, -0.037799, 0.069851, -0.052631, 0.101433, -0.038325, 0.018644, -0.058988, -0.048843, -0.029274, 0.079315, 0.066755, 0.019755, -0.012715, 0.06089, 0.021766, 0.017447, 0.015995, 0.018998, -0.013208, 0.03305, 0.012428, 0.01505, -0.04093, -0.132044, -0.035264, -0.033479, 0.05946, -0.070578, 0.000514, -0.019079, -0.047179, -0.054796, -0.100971, -0.020525, -0.134538, 0.053062, -0.023043, -0.020602, 0.013214, -0.006455, -0.104387, 0.031187, 0.079732, 0.007989, 0.024518, 1.3867e-33, -0.013965, 0.041949, 0.008109, 0.037002, 0.072333, -0.016775, -0.011283, -0.107011, -0.09913, 0.05747, -0.055104, 0.014418, 0.007826, 0.035289, 0.014108, -0.084076, 0.012282, 0.000176, -0.084882, 0.011905, -0.024416, -0.012512, 0.033489, -0.048052, -0.01924, 0.020635, -0.111069, 0.00745, 0.024936, 0.026581, -0.04044, 0.026642, -0.007218, 0.052131, -0.027928, -0.069486, -0.036498, -0.014839, 0.003706, 0.072703, 0.016124, 0.014113, -0.030583, -0.020418, -0.070632, -0.051854, 0.029575, -0.024864, 0.049117, -0.053699, 0.017122, 0.02788, -0.012669, -0.028201, 0.052266, -0.046625, 0.027294, 0.032979, 0.049887, 0.030718, -0.000316, 0.057238, 0.024089, 0.026383, -0.03658, 0.070992, 0.074688, -0.062611, 0.065399, -0.040395, 0.005701, 0.05166, 0.043032, 0.019983, 0.050351, 0.006587, 0.031543, 0.07645, -0.025486, -0.031392, -0.063568, -0.083924, -0.007298, 0.053036, 0.015939, -0.00838, 0.038113, -0.043831, -0.061393, 0.037627, -0.090122, -0.032869, -0.011807, -0.059326, 0.04968, -1.1305e-33, 0.123922, -0.022712, 0.052025, -0.027985, 0.11518, 0.02592, 0.101905, 0.091631, 0.024921, 0.095583, 0.109876, 0.021475, -0.102456, -0.007819, 0.041207, 0.096848, 0.093759, -0.036566, -0.027443, -0.050233, 0.036787, -0.063421, 0.02154, 0.103895, -0.006626, -0.00821, 0.065437, -0.054094, -0.031597, 0.001336, -0.0365, -0.012909, -0.011106, -0.007283, -0.005315, -0.004812, 0.052243, 0.119074, 0.036399, 0.000399, 0.007163, -0.009979, -0.055986, -0.002838, 0.042961, -0.077106, -0.080484, 0.00149, -0.030185, 0.040207, 0.011261, 0.006202, 0.050261, 0.005019, -0.007507, -0.00055, 0.019844, 0.014315, -0.101323, 0.013933, -0.003997, -0.010421, 0.010707, -0.000652, 0.054788, 0.053754, -0.011018, -0.083601, -0.103247, 0.078212, -0.058208, -0.021775, -0.034813, 0.041962, -0.003749, -0.079046, -0.081001, 0.064309, 0.030283, 0.087728, -0.094547, -0.028552, 0.010006, 0.025366, 0.18484, 0.028594, 0.015627, 0.128716, -0.008193, 0.058475, -0.008502, -0.053599, 0.00819, 0.059493, 0.056984, -1.7242e-08, -0.029973, -0.01065, 0.009777, -0.024941, 0.055131, 0.035772, -0.054124, -0.045651, 0.062665, -0.005865, 0.045888, -0.010536, 0.034085, -0.034141, -0.045801, -0.029558, -0.048121, -0.008161, -0.031021, -0.066639, -0.0052, 0.04086, -0.023477, -0.003341, -0.007351, 0.011513, -0.041883, 0.078883, 0.004945, -0.070312, 0.026609, -0.015324, 0.011051, -0.040054, 0.097843, -0.039656, 0.066084, -0.01111, 0.034593, 0.066441, -0.008927, 0.001803, -0.026357, 0.009546, 0.037642, 0.018216, -0.019941, -0.021066, -0.080779, -0.01716, 0.067168, -0.055346, -0.038624, 0.042253, 0.017768, -0.013819, -0.045199, -0.009714, -0.035598, 0.044979, -0.013873, -0.043237, 0.018578, 0.023844], 91455473-212e-4c6e-8bec-1da06779ae10, 775be203-1a84-4822-9645-4da98ca2b2d8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSA1121S','DataStax Astra "One Team" Long Sleeve Tee','DSA1121',{'dsa1121.jpg'},[-0.128281, 0.09116, -0.05124, 0.041872, -0.010697, -0.042401, 0.013542, 0.028344, -0.031758, -0.048159, -0.014381, -0.021522, 0.022061, 0.001091, -0.004459, -0.01765, 0.00127, -0.054974, 0.003806, -0.057383, -0.030689, -0.069808, -0.009856, 0.045913, 0.033188, -0.003201, -0.030323, 0.013019, -0.02478, -0.134776, -0.031757, -0.025324, 0.045739, 0.122666, -0.020512, -0.046719, 0.050697, 0.074318, -0.071908, 0.049594, 0.05296, -0.071829, -0.026518, 0.021275, -0.044688, -0.004517, -0.119697, 0.031503, 0.063931, 0.132113, 0.02005, -0.029622, 0.035483, 0.078433, 0.002937, -0.009169, -0.033202, 0.014157, -0.00373, -0.021947, 0.098319, -0.039983, -0.062478, -0.003983, 0.053708, -0.02556, -0.04923, 0.133057, -0.036825, 0.037583, 0.050818, -0.045284, -0.053976, 0.072054, 0.050476, 0.112095, -0.006048, -0.006784, 0.021989, 0.045748, -0.052298, -0.002314, -0.037799, 0.069851, -0.052631, 0.101433, -0.038325, 0.018644, -0.058988, -0.048843, -0.029274, 0.079315, 0.066755, 0.019755, -0.012715, 0.06089, 0.021766, 0.017447, 0.015995, 0.018998, -0.013208, 0.03305, 0.012428, 0.01505, -0.04093, -0.132044, -0.035264, -0.033479, 0.05946, -0.070578, 0.000514, -0.019079, -0.047179, -0.054796, -0.100971, -0.020525, -0.134538, 0.053062, -0.023043, -0.020602, 0.013214, -0.006455, -0.104387, 0.031187, 0.079732, 0.007989, 0.024518, 1.3867e-33, -0.013965, 0.041949, 0.008109, 0.037002, 0.072333, -0.016775, -0.011283, -0.107011, -0.09913, 0.05747, -0.055104, 0.014418, 0.007826, 0.035289, 0.014108, -0.084076, 0.012282, 0.000176, -0.084882, 0.011905, -0.024416, -0.012512, 0.033489, -0.048052, -0.01924, 0.020635, -0.111069, 0.00745, 0.024936, 0.026581, -0.04044, 0.026642, -0.007218, 0.052131, -0.027928, -0.069486, -0.036498, -0.014839, 0.003706, 0.072703, 0.016124, 0.014113, -0.030583, -0.020418, -0.070632, -0.051854, 0.029575, -0.024864, 0.049117, -0.053699, 0.017122, 0.02788, -0.012669, -0.028201, 0.052266, -0.046625, 0.027294, 0.032979, 0.049887, 0.030718, -0.000316, 0.057238, 0.024089, 0.026383, -0.03658, 0.070992, 0.074688, -0.062611, 0.065399, -0.040395, 0.005701, 0.05166, 0.043032, 0.019983, 0.050351, 0.006587, 0.031543, 0.07645, -0.025486, -0.031392, -0.063568, -0.083924, -0.007298, 0.053036, 0.015939, -0.00838, 0.038113, -0.043831, -0.061393, 0.037627, -0.090122, -0.032869, -0.011807, -0.059326, 0.04968, -1.1305e-33, 0.123922, -0.022712, 0.052025, -0.027985, 0.11518, 0.02592, 0.101905, 0.091631, 0.024921, 0.095583, 0.109876, 0.021475, -0.102456, -0.007819, 0.041207, 0.096848, 0.093759, -0.036566, -0.027443, -0.050233, 0.036787, -0.063421, 0.02154, 0.103895, -0.006626, -0.00821, 0.065437, -0.054094, -0.031597, 0.001336, -0.0365, -0.012909, -0.011106, -0.007283, -0.005315, -0.004812, 0.052243, 0.119074, 0.036399, 0.000399, 0.007163, -0.009979, -0.055986, -0.002838, 0.042961, -0.077106, -0.080484, 0.00149, -0.030185, 0.040207, 0.011261, 0.006202, 0.050261, 0.005019, -0.007507, -0.00055, 0.019844, 0.014315, -0.101323, 0.013933, -0.003997, -0.010421, 0.010707, -0.000652, 0.054788, 0.053754, -0.011018, -0.083601, -0.103247, 0.078212, -0.058208, -0.021775, -0.034813, 0.041962, -0.003749, -0.079046, -0.081001, 0.064309, 0.030283, 0.087728, -0.094547, -0.028552, 0.010006, 0.025366, 0.18484, 0.028594, 0.015627, 0.128716, -0.008193, 0.058475, -0.008502, -0.053599, 0.00819, 0.059493, 0.056984, -1.7242e-08, -0.029973, -0.01065, 0.009777, -0.024941, 0.055131, 0.035772, -0.054124, -0.045651, 0.062665, -0.005865, 0.045888, -0.010536, 0.034085, -0.034141, -0.045801, -0.029558, -0.048121, -0.008161, -0.031021, -0.066639, -0.0052, 0.04086, -0.023477, -0.003341, -0.007351, 0.011513, -0.041883, 0.078883, 0.004945, -0.070312, 0.026609, -0.015324, 0.011051, -0.040054, 0.097843, -0.039656, 0.066084, -0.01111, 0.034593, 0.066441, -0.008927, 0.001803, -0.026357, 0.009546, 0.037642, 0.018216, -0.019941, -0.021066, -0.080779, -0.01716, 0.067168, -0.055346, -0.038624, 0.042253, 0.017768, -0.013819, -0.045199, -0.009714, -0.035598, 0.044979, -0.013873, -0.043237, 0.018578, 0.023844], 91455473-212e-4c6e-8bec-1da06779ae10, 775be203-1a84-4822-9645-4da98ca2b2d8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSA1121XL','DataStax Astra "One Team" Long Sleeve Tee','DSA1121',{'dsa1121.jpg'},[-0.128281, 0.09116, -0.05124, 0.041872, -0.010697, -0.042401, 0.013542, 0.028344, -0.031758, -0.048159, -0.014381, -0.021522, 0.022061, 0.001091, -0.004459, -0.01765, 0.00127, -0.054974, 0.003806, -0.057383, -0.030689, -0.069808, -0.009856, 0.045913, 0.033188, -0.003201, -0.030323, 0.013019, -0.02478, -0.134776, -0.031757, -0.025324, 0.045739, 0.122666, -0.020512, -0.046719, 0.050697, 0.074318, -0.071908, 0.049594, 0.05296, -0.071829, -0.026518, 0.021275, -0.044688, -0.004517, -0.119697, 0.031503, 0.063931, 0.132113, 0.02005, -0.029622, 0.035483, 0.078433, 0.002937, -0.009169, -0.033202, 0.014157, -0.00373, -0.021947, 0.098319, -0.039983, -0.062478, -0.003983, 0.053708, -0.02556, -0.04923, 0.133057, -0.036825, 0.037583, 0.050818, -0.045284, -0.053976, 0.072054, 0.050476, 0.112095, -0.006048, -0.006784, 0.021989, 0.045748, -0.052298, -0.002314, -0.037799, 0.069851, -0.052631, 0.101433, -0.038325, 0.018644, -0.058988, -0.048843, -0.029274, 0.079315, 0.066755, 0.019755, -0.012715, 0.06089, 0.021766, 0.017447, 0.015995, 0.018998, -0.013208, 0.03305, 0.012428, 0.01505, -0.04093, -0.132044, -0.035264, -0.033479, 0.05946, -0.070578, 0.000514, -0.019079, -0.047179, -0.054796, -0.100971, -0.020525, -0.134538, 0.053062, -0.023043, -0.020602, 0.013214, -0.006455, -0.104387, 0.031187, 0.079732, 0.007989, 0.024518, 1.3867e-33, -0.013965, 0.041949, 0.008109, 0.037002, 0.072333, -0.016775, -0.011283, -0.107011, -0.09913, 0.05747, -0.055104, 0.014418, 0.007826, 0.035289, 0.014108, -0.084076, 0.012282, 0.000176, -0.084882, 0.011905, -0.024416, -0.012512, 0.033489, -0.048052, -0.01924, 0.020635, -0.111069, 0.00745, 0.024936, 0.026581, -0.04044, 0.026642, -0.007218, 0.052131, -0.027928, -0.069486, -0.036498, -0.014839, 0.003706, 0.072703, 0.016124, 0.014113, -0.030583, -0.020418, -0.070632, -0.051854, 0.029575, -0.024864, 0.049117, -0.053699, 0.017122, 0.02788, -0.012669, -0.028201, 0.052266, -0.046625, 0.027294, 0.032979, 0.049887, 0.030718, -0.000316, 0.057238, 0.024089, 0.026383, -0.03658, 0.070992, 0.074688, -0.062611, 0.065399, -0.040395, 0.005701, 0.05166, 0.043032, 0.019983, 0.050351, 0.006587, 0.031543, 0.07645, -0.025486, -0.031392, -0.063568, -0.083924, -0.007298, 0.053036, 0.015939, -0.00838, 0.038113, -0.043831, -0.061393, 0.037627, -0.090122, -0.032869, -0.011807, -0.059326, 0.04968, -1.1305e-33, 0.123922, -0.022712, 0.052025, -0.027985, 0.11518, 0.02592, 0.101905, 0.091631, 0.024921, 0.095583, 0.109876, 0.021475, -0.102456, -0.007819, 0.041207, 0.096848, 0.093759, -0.036566, -0.027443, -0.050233, 0.036787, -0.063421, 0.02154, 0.103895, -0.006626, -0.00821, 0.065437, -0.054094, -0.031597, 0.001336, -0.0365, -0.012909, -0.011106, -0.007283, -0.005315, -0.004812, 0.052243, 0.119074, 0.036399, 0.000399, 0.007163, -0.009979, -0.055986, -0.002838, 0.042961, -0.077106, -0.080484, 0.00149, -0.030185, 0.040207, 0.011261, 0.006202, 0.050261, 0.005019, -0.007507, -0.00055, 0.019844, 0.014315, -0.101323, 0.013933, -0.003997, -0.010421, 0.010707, -0.000652, 0.054788, 0.053754, -0.011018, -0.083601, -0.103247, 0.078212, -0.058208, -0.021775, -0.034813, 0.041962, -0.003749, -0.079046, -0.081001, 0.064309, 0.030283, 0.087728, -0.094547, -0.028552, 0.010006, 0.025366, 0.18484, 0.028594, 0.015627, 0.128716, -0.008193, 0.058475, -0.008502, -0.053599, 0.00819, 0.059493, 0.056984, -1.7242e-08, -0.029973, -0.01065, 0.009777, -0.024941, 0.055131, 0.035772, -0.054124, -0.045651, 0.062665, -0.005865, 0.045888, -0.010536, 0.034085, -0.034141, -0.045801, -0.029558, -0.048121, -0.008161, -0.031021, -0.066639, -0.0052, 0.04086, -0.023477, -0.003341, -0.007351, 0.011513, -0.041883, 0.078883, 0.004945, -0.070312, 0.026609, -0.015324, 0.011051, -0.040054, 0.097843, -0.039656, 0.066084, -0.01111, 0.034593, 0.066441, -0.008927, 0.001803, -0.026357, 0.009546, 0.037642, 0.018216, -0.019941, -0.021066, -0.080779, -0.01716, 0.067168, -0.055346, -0.038624, 0.042253, 0.017768, -0.013819, -0.045199, -0.009714, -0.035598, 0.044979, -0.013873, -0.043237, 0.018578, 0.023844], 91455473-212e-4c6e-8bec-1da06779ae10, 775be203-1a84-4822-9645-4da98ca2b2d8);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH916XL','DataStax Black Hoodie','DSH916',{'dsh916.jpg'},[-0.129017, 0.067509, -0.015875, 0.048426, 0.023997, -0.061915, 0.08439, -0.014946, -0.014037, -0.01882, 0.04372, -0.042962, 0.036557, -0.073862, 0.004357, -0.045615, 0.021633, -0.077671, -0.016247, -0.072705, -0.068221, -0.099462, -0.007675, 0.035268, -0.045258, 0.045555, 0.025004, 0.041581, -0.019663, -0.05486, -0.003515, -0.061672, -0.009699, 0.05534, -0.003077, -0.094152, 0.069416, 0.035156, -0.108967, 0.053887, -0.050415, -0.049543, -0.085273, -0.02566, 0.004056, -0.05765, 0.036245, 0.030902, 0.072715, 0.032441, -0.010414, -0.053594, -0.004174, 0.085963, -0.05188, -0.027257, -0.000226, 0.063204, 0.020836, -0.040103, 0.016916, -0.051389, -0.005354, 0.093018, 0.074413, 0.041218, -0.049515, -0.043444, 0.024242, -0.018854, 0.06713, 0.003053, -0.067364, 0.047897, -0.050362, 0.062386, 0.059457, -0.044823, 0.039931, 0.064424, -0.032335, 0.023115, 0.004613, 0.102342, 0.021568, 0.030296, -0.053878, -0.002851, -0.056108, -0.010725, -0.003633, 0.085977, -0.047319, 0.037578, -0.062882, -0.010736, 0.010672, -0.020658, 0.050932, 0.069111, -0.063572, -0.064823, -0.005303, 0.051188, -0.035147, -0.057582, -0.004954, 0.021369, 0.007306, -0.037574, -0.08673, 0.020324, -0.066145, 0.016954, -0.018223, -0.082187, -0.046531, 0.055297, -0.026099, -0.026509, -0.032185, -0.035605, -0.080514, -0.057193, -0.017802, -0.049307, -0.003328, 1.649e-33, 0.013645, -0.037282, 0.040852, -0.002331, 0.097641, 0.007424, -0.008513, -0.096368, -0.098756, 0.119852, -0.01464, 0.000601, -0.108841, 0.072425, 0.0884, -0.052623, 0.030745, -0.000711, -0.032612, -0.068671, 0.00961, 0.023441, 0.024298, -0.00484, 0.039726, 0.061706, -0.029063, 0.030185, 0.063927, 0.044258, -0.115545, -0.055408, 0.071472, 0.000871, -0.040447, -0.003288, -0.071295, -0.048677, 0.011611, 0.034181, 0.056351, 0.044125, 0.008132, 0.009064, -0.066895, 0.07329, 0.067981, 0.001894, 0.004687, -0.032135, 0.049458, -0.069185, 0.007026, -0.006924, -0.014516, 0.052085, 0.009412, -0.043722, 0.045857, 0.007783, 0.059719, 0.043825, 0.069913, -0.045914, 0.044959, 0.00805, 0.050918, -0.021053, -0.014731, -0.023229, -0.01198, 0.119345, 0.051672, 0.034226, 0.167833, -0.028407, -0.001305, 0.006338, -0.022531, -0.028635, -0.025985, -0.08917, -0.016697, 0.067119, -0.043823, 0.037993, 0.003838, 0.060349, 0.018687, -0.008926, -0.036176, -0.051176, -0.061908, -0.001707, -0.054756, -9.16e-34, 0.057939, 0.003394, 0.06267, 0.036977, 0.088051, 0.061418, 0.066262, 0.061615, 0.029882, 0.050998, 0.137483, 0.026111, -0.06367, 2.2e-05, 0.068629, 0.100742, 0.019779, 0.016244, -0.03421, -0.061377, -0.030804, -0.013645, -0.031583, -0.031653, -0.050665, -0.034643, -0.000574, 0.116899, -0.013466, 0.007199, -0.064671, 0.016177, -0.035082, 0.053696, 0.046807, 0.032608, 0.066758, 0.029107, 0.037174, -0.004255, -0.005752, -0.039566, -0.008362, 0.072746, -0.026588, -0.043542, 0.00056, 0.015052, 0.044066, 0.000358, -0.018252, -0.016219, 0.093698, 0.165697, 0.018939, 0.006157, 0.014289, 0.02324, -0.034352, 0.03285, 0.051928, 0.013595, -0.086319, 0.005808, 0.006539, -0.032973, 0.011588, -0.074198, -0.061044, 0.031628, 0.076983, -0.06028, 0.022057, 0.035741, 0.016628, -0.104409, -0.051588, 0.067714, 0.028857, 0.057744, 0.017734, -0.019641, 0.026703, 0.030355, 0.118615, 0.051027, 0.023571, 0.08392, 0.043793, -0.029552, -0.050897, 0.022737, -0.031663, 0.059622, 0.012532, -1.4841e-08, 0.025565, -0.007569, 0.072664, -0.014462, 0.022747, -0.026543, -0.074532, -0.0561, 0.053679, -0.013681, 0.073147, 0.025113, -0.003458, -0.058691, -0.153186, -0.009548, -0.006189, 0.016363, 0.064096, -0.066897, -0.010972, 0.033546, 0.031447, -0.061415, 0.039395, 0.031431, -0.027278, 0.054885, 0.008619, 0.021983, 0.0078, -0.007173, 0.050387, -0.09795, 0.048781, -0.050042, -0.056402, 0.03111, 0.022662, -0.072499, 0.048744, -0.078595, 0.016558, -0.039894, -0.030898, 0.034661, -0.012456, 0.01212, -0.034626, 0.038879, 0.048413, -0.065845, -0.056738, 0.054528, -0.040141, -0.102604, -0.000581, 0.042447, -0.064685, 0.019037, -0.046186, -0.071556, -0.025134, -0.023957], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, b9bed3c0-0a76-44ea-bce6-f5f21611a3f1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSS821L','DataStax Gray Track Jacket','DSS821',{'dss821.jpg'},[-0.070822, 0.067412, 0.067837, 0.028603, 0.038284, -0.03795, 0.057943, -0.003419, -0.089906, -0.009409, -0.012202, -0.043371, 0.040934, -0.038866, -0.020301, 0.02537, 0.016403, -0.014058, 0.003802, -0.016225, -0.052727, -0.055283, -0.060209, -0.015198, -0.098195, 0.085139, 0.000766, 0.054204, -0.03671, -0.050062, -0.053456, -0.055069, 0.013894, 0.092321, -0.02926, -0.11422, 0.033829, 0.041433, -0.163577, 0.111164, -0.02939, -0.029282, -0.053552, -0.020903, 0.005898, -0.019796, -0.025593, -0.047166, -0.013557, 0.106381, -0.033793, -0.034029, 0.026414, 0.051884, 0.0278, 0.033797, 0.030605, 0.079492, -0.032727, -0.052188, 0.008066, -0.016971, -0.015393, -0.034827, 0.103859, 0.055414, -0.074988, 0.022946, 0.015255, -0.021385, 0.085306, -0.042407, -0.005371, -0.002313, -0.032112, 0.120115, -0.017134, 0.015671, 0.063326, -0.088181, -0.043913, 0.007295, -0.009737, 0.025253, 0.06912, 0.07063, -0.015104, -0.00241, -0.057653, -0.003439, 0.009428, 0.067399, -0.062591, 0.041882, -0.03421, 0.01959, -0.053489, 0.052838, 0.013058, -0.002756, -0.000566, -0.040461, -0.027297, 0.078289, -0.093007, -0.105511, 0.041449, -0.003332, -0.033108, -0.061364, -0.015483, 0.00982, -0.061284, 0.019781, -0.029703, -0.042375, -0.091103, 0.071578, -0.01625, -0.044078, -0.004867, -0.00565, -0.057703, -0.004691, -0.025308, -0.072328, 0.030312, 1.8689e-33, 0.015913, -0.03778, -0.030665, -0.020792, 0.045622, -0.03749, -0.065968, -0.09877, -0.076629, 0.066026, -0.006828, 0.072913, -0.06087, 0.068782, -0.005168, -0.046433, 0.026315, 0.055078, -0.04089, -0.003496, 0.027407, 0.014074, 0.01172, -0.073333, 0.102007, 0.080958, -0.031094, -0.006801, 0.04103, 0.027227, -0.082117, -0.069445, 0.082866, 0.053655, -0.031301, 0.022624, -0.031269, 0.056532, 0.002579, 0.026025, 0.023103, 0.046364, 0.053285, 0.00738, -0.076094, 0.053825, 0.117662, -0.033342, -0.003152, -0.030549, 0.009456, -0.044904, 0.024666, -0.06067, 0.00278, -0.029093, 0.049255, 0.016163, 0.021089, -0.052359, 0.028282, 0.056396, 0.044063, -0.090298, 0.09749, 0.059281, 0.038587, -0.053988, -0.028017, -0.011419, 0.007427, 0.061615, 0.104077, 0.047941, 0.136658, -0.048971, -0.027562, 0.01873, -0.08155, -0.021481, -0.102228, -0.07763, -0.055903, 0.032897, 0.018061, -0.011516, 0.022762, 0.074069, -0.055565, -0.026623, 0.00789, -0.009417, -0.077691, 0.000666, -0.033632, -1.4329e-33, 0.094305, 0.026841, 0.093368, 0.065417, 0.034854, 0.073414, 0.063908, 0.052555, 0.05267, 0.07461, 0.124084, -0.029458, -0.038282, -0.001495, -0.008595, 0.058036, -0.031594, 0.058387, -0.006543, -0.097112, -0.022557, -0.067217, -0.024157, 0.090862, 0.026418, -0.015968, 0.084917, 0.044075, -0.04996, -0.085729, -0.055774, 0.011344, -0.036554, -0.053064, 0.035734, 0.021267, 0.094037, 0.049557, -0.020078, -0.019716, -0.071952, 0.00118, 0.028194, 0.032138, -0.01243, -0.027777, 0.003696, 0.076085, -0.029231, 0.026018, 0.067993, -0.014775, 0.101935, 0.038735, 0.031708, 0.064141, -0.054154, 0.043146, -0.056811, 0.053663, 0.044735, -0.001209, -0.071045, -0.015353, -0.005951, 0.009402, -0.017787, -0.063602, -0.061653, 0.0295, 0.030161, -0.005881, 0.004659, 0.024536, 0.033438, -0.119971, 0.026318, 0.077991, 0.050921, 0.058379, 0.024528, -0.020942, 0.068229, -0.020859, 0.117472, 0.103836, 0.004474, 0.058364, 0.010027, -0.059164, -0.022552, -0.002064, -0.049125, 0.124097, -0.076038, -1.4415e-08, -0.010327, 0.006619, 0.023207, -0.047941, -0.03786, -0.007745, -0.003859, 0.019159, 0.044095, -0.041403, 0.088419, -0.022699, -0.055371, 0.036499, -0.054483, -0.043757, -0.015927, 0.04443, 0.019716, -0.023849, -0.03087, 0.002368, -0.018692, 0.01996, 0.018036, 0.036074, 0.029236, 0.031627, 0.060087, -0.008958, -0.013413, -0.002275, 0.049675, -0.043193, 0.053736, -0.053504, -0.029948, 0.075683, 0.063538, -0.026483, -0.021259, 0.001404, 0.012944, -0.034055, 0.021664, -0.042476, -0.002874, -0.013281, -0.050269, -0.017104, 0.068875, -0.043487, -0.021892, 0.046017, 0.007241, -0.031531, 0.009856, 0.042387, -0.055597, 0.035617, -0.069966, -0.106963, -0.019438, 0.048806], d887b049-d16c-46e1-8c94-0a1280dedc30, f629e107-b219-4563-a852-6909fd246949);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSS821M','DataStax Gray Track Jacket','DSS821',{'dss821.jpg'},[-0.070822, 0.067412, 0.067837, 0.028603, 0.038284, -0.03795, 0.057943, -0.003419, -0.089906, -0.009409, -0.012202, -0.043371, 0.040934, -0.038866, -0.020301, 0.02537, 0.016403, -0.014058, 0.003802, -0.016225, -0.052727, -0.055283, -0.060209, -0.015198, -0.098195, 0.085139, 0.000766, 0.054204, -0.03671, -0.050062, -0.053456, -0.055069, 0.013894, 0.092321, -0.02926, -0.11422, 0.033829, 0.041433, -0.163577, 0.111164, -0.02939, -0.029282, -0.053552, -0.020903, 0.005898, -0.019796, -0.025593, -0.047166, -0.013557, 0.106381, -0.033793, -0.034029, 0.026414, 0.051884, 0.0278, 0.033797, 0.030605, 0.079492, -0.032727, -0.052188, 0.008066, -0.016971, -0.015393, -0.034827, 0.103859, 0.055414, -0.074988, 0.022946, 0.015255, -0.021385, 0.085306, -0.042407, -0.005371, -0.002313, -0.032112, 0.120115, -0.017134, 0.015671, 0.063326, -0.088181, -0.043913, 0.007295, -0.009737, 0.025253, 0.06912, 0.07063, -0.015104, -0.00241, -0.057653, -0.003439, 0.009428, 0.067399, -0.062591, 0.041882, -0.03421, 0.01959, -0.053489, 0.052838, 0.013058, -0.002756, -0.000566, -0.040461, -0.027297, 0.078289, -0.093007, -0.105511, 0.041449, -0.003332, -0.033108, -0.061364, -0.015483, 0.00982, -0.061284, 0.019781, -0.029703, -0.042375, -0.091103, 0.071578, -0.01625, -0.044078, -0.004867, -0.00565, -0.057703, -0.004691, -0.025308, -0.072328, 0.030312, 1.8689e-33, 0.015913, -0.03778, -0.030665, -0.020792, 0.045622, -0.03749, -0.065968, -0.09877, -0.076629, 0.066026, -0.006828, 0.072913, -0.06087, 0.068782, -0.005168, -0.046433, 0.026315, 0.055078, -0.04089, -0.003496, 0.027407, 0.014074, 0.01172, -0.073333, 0.102007, 0.080958, -0.031094, -0.006801, 0.04103, 0.027227, -0.082117, -0.069445, 0.082866, 0.053655, -0.031301, 0.022624, -0.031269, 0.056532, 0.002579, 0.026025, 0.023103, 0.046364, 0.053285, 0.00738, -0.076094, 0.053825, 0.117662, -0.033342, -0.003152, -0.030549, 0.009456, -0.044904, 0.024666, -0.06067, 0.00278, -0.029093, 0.049255, 0.016163, 0.021089, -0.052359, 0.028282, 0.056396, 0.044063, -0.090298, 0.09749, 0.059281, 0.038587, -0.053988, -0.028017, -0.011419, 0.007427, 0.061615, 0.104077, 0.047941, 0.136658, -0.048971, -0.027562, 0.01873, -0.08155, -0.021481, -0.102228, -0.07763, -0.055903, 0.032897, 0.018061, -0.011516, 0.022762, 0.074069, -0.055565, -0.026623, 0.00789, -0.009417, -0.077691, 0.000666, -0.033632, -1.4329e-33, 0.094305, 0.026841, 0.093368, 0.065417, 0.034854, 0.073414, 0.063908, 0.052555, 0.05267, 0.07461, 0.124084, -0.029458, -0.038282, -0.001495, -0.008595, 0.058036, -0.031594, 0.058387, -0.006543, -0.097112, -0.022557, -0.067217, -0.024157, 0.090862, 0.026418, -0.015968, 0.084917, 0.044075, -0.04996, -0.085729, -0.055774, 0.011344, -0.036554, -0.053064, 0.035734, 0.021267, 0.094037, 0.049557, -0.020078, -0.019716, -0.071952, 0.00118, 0.028194, 0.032138, -0.01243, -0.027777, 0.003696, 0.076085, -0.029231, 0.026018, 0.067993, -0.014775, 0.101935, 0.038735, 0.031708, 0.064141, -0.054154, 0.043146, -0.056811, 0.053663, 0.044735, -0.001209, -0.071045, -0.015353, -0.005951, 0.009402, -0.017787, -0.063602, -0.061653, 0.0295, 0.030161, -0.005881, 0.004659, 0.024536, 0.033438, -0.119971, 0.026318, 0.077991, 0.050921, 0.058379, 0.024528, -0.020942, 0.068229, -0.020859, 0.117472, 0.103836, 0.004474, 0.058364, 0.010027, -0.059164, -0.022552, -0.002064, -0.049125, 0.124097, -0.076038, -1.4415e-08, -0.010327, 0.006619, 0.023207, -0.047941, -0.03786, -0.007745, -0.003859, 0.019159, 0.044095, -0.041403, 0.088419, -0.022699, -0.055371, 0.036499, -0.054483, -0.043757, -0.015927, 0.04443, 0.019716, -0.023849, -0.03087, 0.002368, -0.018692, 0.01996, 0.018036, 0.036074, 0.029236, 0.031627, 0.060087, -0.008958, -0.013413, -0.002275, 0.049675, -0.043193, 0.053736, -0.053504, -0.029948, 0.075683, 0.063538, -0.026483, -0.021259, 0.001404, 0.012944, -0.034055, 0.021664, -0.042476, -0.002874, -0.013281, -0.050269, -0.017104, 0.068875, -0.043487, -0.021892, 0.046017, 0.007241, -0.031531, 0.009856, 0.042387, -0.055597, 0.035617, -0.069966, -0.106963, -0.019438, 0.048806], d887b049-d16c-46e1-8c94-0a1280dedc30, f629e107-b219-4563-a852-6909fd246949);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSS821S','DataStax Gray Track Jacket','DSS821',{'dss821.jpg'},[-0.070822, 0.067412, 0.067837, 0.028603, 0.038284, -0.03795, 0.057943, -0.003419, -0.089906, -0.009409, -0.012202, -0.043371, 0.040934, -0.038866, -0.020301, 0.02537, 0.016403, -0.014058, 0.003802, -0.016225, -0.052727, -0.055283, -0.060209, -0.015198, -0.098195, 0.085139, 0.000766, 0.054204, -0.03671, -0.050062, -0.053456, -0.055069, 0.013894, 0.092321, -0.02926, -0.11422, 0.033829, 0.041433, -0.163577, 0.111164, -0.02939, -0.029282, -0.053552, -0.020903, 0.005898, -0.019796, -0.025593, -0.047166, -0.013557, 0.106381, -0.033793, -0.034029, 0.026414, 0.051884, 0.0278, 0.033797, 0.030605, 0.079492, -0.032727, -0.052188, 0.008066, -0.016971, -0.015393, -0.034827, 0.103859, 0.055414, -0.074988, 0.022946, 0.015255, -0.021385, 0.085306, -0.042407, -0.005371, -0.002313, -0.032112, 0.120115, -0.017134, 0.015671, 0.063326, -0.088181, -0.043913, 0.007295, -0.009737, 0.025253, 0.06912, 0.07063, -0.015104, -0.00241, -0.057653, -0.003439, 0.009428, 0.067399, -0.062591, 0.041882, -0.03421, 0.01959, -0.053489, 0.052838, 0.013058, -0.002756, -0.000566, -0.040461, -0.027297, 0.078289, -0.093007, -0.105511, 0.041449, -0.003332, -0.033108, -0.061364, -0.015483, 0.00982, -0.061284, 0.019781, -0.029703, -0.042375, -0.091103, 0.071578, -0.01625, -0.044078, -0.004867, -0.00565, -0.057703, -0.004691, -0.025308, -0.072328, 0.030312, 1.8689e-33, 0.015913, -0.03778, -0.030665, -0.020792, 0.045622, -0.03749, -0.065968, -0.09877, -0.076629, 0.066026, -0.006828, 0.072913, -0.06087, 0.068782, -0.005168, -0.046433, 0.026315, 0.055078, -0.04089, -0.003496, 0.027407, 0.014074, 0.01172, -0.073333, 0.102007, 0.080958, -0.031094, -0.006801, 0.04103, 0.027227, -0.082117, -0.069445, 0.082866, 0.053655, -0.031301, 0.022624, -0.031269, 0.056532, 0.002579, 0.026025, 0.023103, 0.046364, 0.053285, 0.00738, -0.076094, 0.053825, 0.117662, -0.033342, -0.003152, -0.030549, 0.009456, -0.044904, 0.024666, -0.06067, 0.00278, -0.029093, 0.049255, 0.016163, 0.021089, -0.052359, 0.028282, 0.056396, 0.044063, -0.090298, 0.09749, 0.059281, 0.038587, -0.053988, -0.028017, -0.011419, 0.007427, 0.061615, 0.104077, 0.047941, 0.136658, -0.048971, -0.027562, 0.01873, -0.08155, -0.021481, -0.102228, -0.07763, -0.055903, 0.032897, 0.018061, -0.011516, 0.022762, 0.074069, -0.055565, -0.026623, 0.00789, -0.009417, -0.077691, 0.000666, -0.033632, -1.4329e-33, 0.094305, 0.026841, 0.093368, 0.065417, 0.034854, 0.073414, 0.063908, 0.052555, 0.05267, 0.07461, 0.124084, -0.029458, -0.038282, -0.001495, -0.008595, 0.058036, -0.031594, 0.058387, -0.006543, -0.097112, -0.022557, -0.067217, -0.024157, 0.090862, 0.026418, -0.015968, 0.084917, 0.044075, -0.04996, -0.085729, -0.055774, 0.011344, -0.036554, -0.053064, 0.035734, 0.021267, 0.094037, 0.049557, -0.020078, -0.019716, -0.071952, 0.00118, 0.028194, 0.032138, -0.01243, -0.027777, 0.003696, 0.076085, -0.029231, 0.026018, 0.067993, -0.014775, 0.101935, 0.038735, 0.031708, 0.064141, -0.054154, 0.043146, -0.056811, 0.053663, 0.044735, -0.001209, -0.071045, -0.015353, -0.005951, 0.009402, -0.017787, -0.063602, -0.061653, 0.0295, 0.030161, -0.005881, 0.004659, 0.024536, 0.033438, -0.119971, 0.026318, 0.077991, 0.050921, 0.058379, 0.024528, -0.020942, 0.068229, -0.020859, 0.117472, 0.103836, 0.004474, 0.058364, 0.010027, -0.059164, -0.022552, -0.002064, -0.049125, 0.124097, -0.076038, -1.4415e-08, -0.010327, 0.006619, 0.023207, -0.047941, -0.03786, -0.007745, -0.003859, 0.019159, 0.044095, -0.041403, 0.088419, -0.022699, -0.055371, 0.036499, -0.054483, -0.043757, -0.015927, 0.04443, 0.019716, -0.023849, -0.03087, 0.002368, -0.018692, 0.01996, 0.018036, 0.036074, 0.029236, 0.031627, 0.060087, -0.008958, -0.013413, -0.002275, 0.049675, -0.043193, 0.053736, -0.053504, -0.029948, 0.075683, 0.063538, -0.026483, -0.021259, 0.001404, 0.012944, -0.034055, 0.021664, -0.042476, -0.002874, -0.013281, -0.050269, -0.017104, 0.068875, -0.043487, -0.021892, 0.046017, 0.007241, -0.031531, 0.009856, 0.042387, -0.055597, 0.035617, -0.069966, -0.106963, -0.019438, 0.048806], d887b049-d16c-46e1-8c94-0a1280dedc30, f629e107-b219-4563-a852-6909fd246949);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSS821XL','DataStax Gray Track Jacket','DSS821',{'dss821.jpg'},[-0.070822, 0.067412, 0.067837, 0.028603, 0.038284, -0.03795, 0.057943, -0.003419, -0.089906, -0.009409, -0.012202, -0.043371, 0.040934, -0.038866, -0.020301, 0.02537, 0.016403, -0.014058, 0.003802, -0.016225, -0.052727, -0.055283, -0.060209, -0.015198, -0.098195, 0.085139, 0.000766, 0.054204, -0.03671, -0.050062, -0.053456, -0.055069, 0.013894, 0.092321, -0.02926, -0.11422, 0.033829, 0.041433, -0.163577, 0.111164, -0.02939, -0.029282, -0.053552, -0.020903, 0.005898, -0.019796, -0.025593, -0.047166, -0.013557, 0.106381, -0.033793, -0.034029, 0.026414, 0.051884, 0.0278, 0.033797, 0.030605, 0.079492, -0.032727, -0.052188, 0.008066, -0.016971, -0.015393, -0.034827, 0.103859, 0.055414, -0.074988, 0.022946, 0.015255, -0.021385, 0.085306, -0.042407, -0.005371, -0.002313, -0.032112, 0.120115, -0.017134, 0.015671, 0.063326, -0.088181, -0.043913, 0.007295, -0.009737, 0.025253, 0.06912, 0.07063, -0.015104, -0.00241, -0.057653, -0.003439, 0.009428, 0.067399, -0.062591, 0.041882, -0.03421, 0.01959, -0.053489, 0.052838, 0.013058, -0.002756, -0.000566, -0.040461, -0.027297, 0.078289, -0.093007, -0.105511, 0.041449, -0.003332, -0.033108, -0.061364, -0.015483, 0.00982, -0.061284, 0.019781, -0.029703, -0.042375, -0.091103, 0.071578, -0.01625, -0.044078, -0.004867, -0.00565, -0.057703, -0.004691, -0.025308, -0.072328, 0.030312, 1.8689e-33, 0.015913, -0.03778, -0.030665, -0.020792, 0.045622, -0.03749, -0.065968, -0.09877, -0.076629, 0.066026, -0.006828, 0.072913, -0.06087, 0.068782, -0.005168, -0.046433, 0.026315, 0.055078, -0.04089, -0.003496, 0.027407, 0.014074, 0.01172, -0.073333, 0.102007, 0.080958, -0.031094, -0.006801, 0.04103, 0.027227, -0.082117, -0.069445, 0.082866, 0.053655, -0.031301, 0.022624, -0.031269, 0.056532, 0.002579, 0.026025, 0.023103, 0.046364, 0.053285, 0.00738, -0.076094, 0.053825, 0.117662, -0.033342, -0.003152, -0.030549, 0.009456, -0.044904, 0.024666, -0.06067, 0.00278, -0.029093, 0.049255, 0.016163, 0.021089, -0.052359, 0.028282, 0.056396, 0.044063, -0.090298, 0.09749, 0.059281, 0.038587, -0.053988, -0.028017, -0.011419, 0.007427, 0.061615, 0.104077, 0.047941, 0.136658, -0.048971, -0.027562, 0.01873, -0.08155, -0.021481, -0.102228, -0.07763, -0.055903, 0.032897, 0.018061, -0.011516, 0.022762, 0.074069, -0.055565, -0.026623, 0.00789, -0.009417, -0.077691, 0.000666, -0.033632, -1.4329e-33, 0.094305, 0.026841, 0.093368, 0.065417, 0.034854, 0.073414, 0.063908, 0.052555, 0.05267, 0.07461, 0.124084, -0.029458, -0.038282, -0.001495, -0.008595, 0.058036, -0.031594, 0.058387, -0.006543, -0.097112, -0.022557, -0.067217, -0.024157, 0.090862, 0.026418, -0.015968, 0.084917, 0.044075, -0.04996, -0.085729, -0.055774, 0.011344, -0.036554, -0.053064, 0.035734, 0.021267, 0.094037, 0.049557, -0.020078, -0.019716, -0.071952, 0.00118, 0.028194, 0.032138, -0.01243, -0.027777, 0.003696, 0.076085, -0.029231, 0.026018, 0.067993, -0.014775, 0.101935, 0.038735, 0.031708, 0.064141, -0.054154, 0.043146, -0.056811, 0.053663, 0.044735, -0.001209, -0.071045, -0.015353, -0.005951, 0.009402, -0.017787, -0.063602, -0.061653, 0.0295, 0.030161, -0.005881, 0.004659, 0.024536, 0.033438, -0.119971, 0.026318, 0.077991, 0.050921, 0.058379, 0.024528, -0.020942, 0.068229, -0.020859, 0.117472, 0.103836, 0.004474, 0.058364, 0.010027, -0.059164, -0.022552, -0.002064, -0.049125, 0.124097, -0.076038, -1.4415e-08, -0.010327, 0.006619, 0.023207, -0.047941, -0.03786, -0.007745, -0.003859, 0.019159, 0.044095, -0.041403, 0.088419, -0.022699, -0.055371, 0.036499, -0.054483, -0.043757, -0.015927, 0.04443, 0.019716, -0.023849, -0.03087, 0.002368, -0.018692, 0.01996, 0.018036, 0.036074, 0.029236, 0.031627, 0.060087, -0.008958, -0.013413, -0.002275, 0.049675, -0.043193, 0.053736, -0.053504, -0.029948, 0.075683, 0.063538, -0.026483, -0.021259, 0.001404, 0.012944, -0.034055, 0.021664, -0.042476, -0.002874, -0.013281, -0.050269, -0.017104, 0.068875, -0.043487, -0.021892, 0.046017, 0.007241, -0.031531, 0.009856, 0.042387, -0.055597, 0.035617, -0.069966, -0.106963, -0.019438, 0.048806], d887b049-d16c-46e1-8c94-0a1280dedc30, f629e107-b219-4563-a852-6909fd246949);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH915L','DataStax Vintage 2015 MVP Hoodie','DSH915',{'dsh915.jpg'},[-0.130689, 0.119207, 0.002184, 0.011354, 0.01946, -0.025444, 0.051016, 0.003948, -0.021649, -0.008893, 0.03938, -0.023064, 0.074983, -0.056983, 0.021864, -0.013808, 0.003896, -0.043946, 0.00557, -0.065687, -0.048401, -0.130076, -0.033392, 0.033522, -0.029537, 0.071937, -0.026538, 0.024381, -0.017099, -0.035164, 0.022241, -0.067904, 0.007624, 0.050735, -0.000709, -0.062166, 0.03406, 0.038965, -0.075732, 0.043013, -0.028261, -0.051797, -0.115516, -0.066065, 0.045676, -0.056283, -0.019509, 0.05042, 0.081124, 0.038154, -0.014334, -0.058606, 0.03792, 0.027637, -0.002735, 0.016271, 0.01937, 0.018362, 0.029686, -0.002795, 0.022806, -0.081661, -0.022419, 0.059888, 0.027731, -0.015354, -0.048034, 0.004533, 0.024668, -0.044292, 0.015831, -0.027527, -0.090051, 0.031745, 0.003792, 0.042534, 0.02901, -0.050633, 0.020763, 0.037459, -0.02709, 0.040522, 0.020051, 0.082612, 0.012042, 0.007298, -0.067962, -0.022763, -0.021077, 0.034285, -0.020826, 0.033998, -0.05198, 0.063462, -0.022894, -0.000748, -0.010592, -0.045934, 0.060046, 0.087123, -0.074907, -0.071163, 0.00096, 0.023439, -0.036156, -0.046947, 0.038443, 0.040775, -0.027853, -0.014929, -0.057829, -0.020475, -0.042197, 0.010614, -0.02505, -0.067266, -0.07943, 0.070478, -0.033844, -0.092788, -0.019413, -0.04908, -0.024522, -0.033398, -0.036412, -0.05483, 0.008169, 3.1222e-33, 0.031554, 0.000771, 0.007574, 0.051644, 0.073467, -0.009317, 0.030396, -0.062817, -0.049469, 0.044147, 0.034081, 0.025187, -0.088969, -0.006547, 0.062778, -0.061899, -0.036726, 0.003266, -0.075354, -0.06239, -0.036646, -0.02648, 0.05139, -0.00278, 0.044895, 0.053791, -0.002873, -0.001813, 0.062168, 0.061544, -0.048848, -0.075151, 0.064802, 0.026789, -0.051642, 0.032601, -0.071421, -0.081286, 0.046636, 0.03493, 0.035291, 0.049091, 0.042534, 0.037599, -0.112757, 0.074343, 0.078106, 0.062747, 0.03701, -0.051521, 0.073341, 0.004294, -0.033282, -0.06057, -0.106117, 0.069402, 0.002812, -0.033351, 0.069086, 0.007338, 0.088768, 0.080587, 0.07143, -0.041803, 0.048226, 0.055912, 0.035525, -0.034257, -0.016601, 0.001304, 0.025647, 0.090811, 0.047174, 0.0274, 0.192374, -0.031598, 0.005798, 0.016118, -0.028017, -0.050514, -0.032982, -0.030666, -0.008585, 0.044641, 0.009376, -0.004384, 0.030215, 0.053329, 0.036001, 0.010282, -0.007498, -0.018606, -0.00543, 0.007872, -0.037521, -2.1325e-33, 0.045292, 0.034009, 0.079367, 0.013836, 0.129459, 0.015866, -0.012412, 0.073302, 0.026911, 0.039113, 0.136262, 0.010892, -0.100126, 0.020107, 0.083099, 0.086335, -0.010954, -0.078355, -0.127925, -0.081352, 0.017241, 0.003089, -0.037428, -0.00695, -0.091849, -0.012662, -0.008052, 0.114571, 0.024061, 0.023203, -0.055585, 0.03642, -0.010498, 0.046685, 0.037599, 0.036584, 0.060719, -0.018676, 0.005441, -0.007818, -0.034832, -0.062962, -0.025797, 0.061391, 0.013985, -0.02157, 0.031953, -0.03756, 0.10372, -0.010287, 0.017564, 0.018405, 0.074809, 0.162787, -0.061577, 0.020899, -0.024804, 0.049828, 0.006541, -0.041677, 0.056903, -0.016523, -0.034499, 0.00898, 0.050464, -0.043226, 0.0234, -0.118519, -0.117186, 0.060579, 0.074162, -0.047149, -0.006267, -0.002142, 0.025712, -0.031146, -0.045877, 0.087351, 0.019622, 0.024995, -0.007961, 0.007968, 0.014224, 0.010567, 0.117345, 0.057731, 0.017271, 0.033379, -0.038631, 0.052585, -0.067995, -0.007751, -0.018212, 0.060533, 0.034478, -1.7857e-08, 0.024968, 0.059021, 0.025095, -0.013359, 0.015754, 0.011613, -0.102571, -0.026736, 0.088794, -0.016668, 0.069948, 0.026237, 0.002091, -0.062308, -0.118881, -0.05263, -0.073112, 0.049371, 0.012477, -0.056494, 0.040603, 0.031014, 0.028105, -0.056266, -0.006609, 0.017164, -0.029324, 0.039515, -0.033209, -0.039764, 0.039809, 0.002515, 0.049539, -0.066961, 0.018707, -0.028472, -0.078797, 0.02635, 0.039791, -0.044775, 0.05594, -0.104986, -0.006516, -0.006211, 0.010965, 0.065731, 0.005421, 0.003755, -0.083055, 0.035492, 0.016322, -0.076782, -0.047287, 0.020692, -0.056092, -0.083851, -0.020714, 0.029802, -0.058987, 0.008525, -0.012308, -0.062131, 0.006247, -0.017416], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, 86d234a4-6b97-476c-ada8-efb344d39743);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH915M','DataStax Vintage 2015 MVP Hoodie','DSH915',{'dsh915.jpg'},[-0.130689, 0.119207, 0.002184, 0.011354, 0.01946, -0.025444, 0.051016, 0.003948, -0.021649, -0.008893, 0.03938, -0.023064, 0.074983, -0.056983, 0.021864, -0.013808, 0.003896, -0.043946, 0.00557, -0.065687, -0.048401, -0.130076, -0.033392, 0.033522, -0.029537, 0.071937, -0.026538, 0.024381, -0.017099, -0.035164, 0.022241, -0.067904, 0.007624, 0.050735, -0.000709, -0.062166, 0.03406, 0.038965, -0.075732, 0.043013, -0.028261, -0.051797, -0.115516, -0.066065, 0.045676, -0.056283, -0.019509, 0.05042, 0.081124, 0.038154, -0.014334, -0.058606, 0.03792, 0.027637, -0.002735, 0.016271, 0.01937, 0.018362, 0.029686, -0.002795, 0.022806, -0.081661, -0.022419, 0.059888, 0.027731, -0.015354, -0.048034, 0.004533, 0.024668, -0.044292, 0.015831, -0.027527, -0.090051, 0.031745, 0.003792, 0.042534, 0.02901, -0.050633, 0.020763, 0.037459, -0.02709, 0.040522, 0.020051, 0.082612, 0.012042, 0.007298, -0.067962, -0.022763, -0.021077, 0.034285, -0.020826, 0.033998, -0.05198, 0.063462, -0.022894, -0.000748, -0.010592, -0.045934, 0.060046, 0.087123, -0.074907, -0.071163, 0.00096, 0.023439, -0.036156, -0.046947, 0.038443, 0.040775, -0.027853, -0.014929, -0.057829, -0.020475, -0.042197, 0.010614, -0.02505, -0.067266, -0.07943, 0.070478, -0.033844, -0.092788, -0.019413, -0.04908, -0.024522, -0.033398, -0.036412, -0.05483, 0.008169, 3.1222e-33, 0.031554, 0.000771, 0.007574, 0.051644, 0.073467, -0.009317, 0.030396, -0.062817, -0.049469, 0.044147, 0.034081, 0.025187, -0.088969, -0.006547, 0.062778, -0.061899, -0.036726, 0.003266, -0.075354, -0.06239, -0.036646, -0.02648, 0.05139, -0.00278, 0.044895, 0.053791, -0.002873, -0.001813, 0.062168, 0.061544, -0.048848, -0.075151, 0.064802, 0.026789, -0.051642, 0.032601, -0.071421, -0.081286, 0.046636, 0.03493, 0.035291, 0.049091, 0.042534, 0.037599, -0.112757, 0.074343, 0.078106, 0.062747, 0.03701, -0.051521, 0.073341, 0.004294, -0.033282, -0.06057, -0.106117, 0.069402, 0.002812, -0.033351, 0.069086, 0.007338, 0.088768, 0.080587, 0.07143, -0.041803, 0.048226, 0.055912, 0.035525, -0.034257, -0.016601, 0.001304, 0.025647, 0.090811, 0.047174, 0.0274, 0.192374, -0.031598, 0.005798, 0.016118, -0.028017, -0.050514, -0.032982, -0.030666, -0.008585, 0.044641, 0.009376, -0.004384, 0.030215, 0.053329, 0.036001, 0.010282, -0.007498, -0.018606, -0.00543, 0.007872, -0.037521, -2.1325e-33, 0.045292, 0.034009, 0.079367, 0.013836, 0.129459, 0.015866, -0.012412, 0.073302, 0.026911, 0.039113, 0.136262, 0.010892, -0.100126, 0.020107, 0.083099, 0.086335, -0.010954, -0.078355, -0.127925, -0.081352, 0.017241, 0.003089, -0.037428, -0.00695, -0.091849, -0.012662, -0.008052, 0.114571, 0.024061, 0.023203, -0.055585, 0.03642, -0.010498, 0.046685, 0.037599, 0.036584, 0.060719, -0.018676, 0.005441, -0.007818, -0.034832, -0.062962, -0.025797, 0.061391, 0.013985, -0.02157, 0.031953, -0.03756, 0.10372, -0.010287, 0.017564, 0.018405, 0.074809, 0.162787, -0.061577, 0.020899, -0.024804, 0.049828, 0.006541, -0.041677, 0.056903, -0.016523, -0.034499, 0.00898, 0.050464, -0.043226, 0.0234, -0.118519, -0.117186, 0.060579, 0.074162, -0.047149, -0.006267, -0.002142, 0.025712, -0.031146, -0.045877, 0.087351, 0.019622, 0.024995, -0.007961, 0.007968, 0.014224, 0.010567, 0.117345, 0.057731, 0.017271, 0.033379, -0.038631, 0.052585, -0.067995, -0.007751, -0.018212, 0.060533, 0.034478, -1.7857e-08, 0.024968, 0.059021, 0.025095, -0.013359, 0.015754, 0.011613, -0.102571, -0.026736, 0.088794, -0.016668, 0.069948, 0.026237, 0.002091, -0.062308, -0.118881, -0.05263, -0.073112, 0.049371, 0.012477, -0.056494, 0.040603, 0.031014, 0.028105, -0.056266, -0.006609, 0.017164, -0.029324, 0.039515, -0.033209, -0.039764, 0.039809, 0.002515, 0.049539, -0.066961, 0.018707, -0.028472, -0.078797, 0.02635, 0.039791, -0.044775, 0.05594, -0.104986, -0.006516, -0.006211, 0.010965, 0.065731, 0.005421, 0.003755, -0.083055, 0.035492, 0.016322, -0.076782, -0.047287, 0.020692, -0.056092, -0.083851, -0.020714, 0.029802, -0.058987, 0.008525, -0.012308, -0.062131, 0.006247, -0.017416], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, 86d234a4-6b97-476c-ada8-efb344d39743);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH915S','DataStax Vintage 2015 MVP Hoodie','DSH915',{'dsh915.jpg'},[-0.130689, 0.119207, 0.002184, 0.011354, 0.01946, -0.025444, 0.051016, 0.003948, -0.021649, -0.008893, 0.03938, -0.023064, 0.074983, -0.056983, 0.021864, -0.013808, 0.003896, -0.043946, 0.00557, -0.065687, -0.048401, -0.130076, -0.033392, 0.033522, -0.029537, 0.071937, -0.026538, 0.024381, -0.017099, -0.035164, 0.022241, -0.067904, 0.007624, 0.050735, -0.000709, -0.062166, 0.03406, 0.038965, -0.075732, 0.043013, -0.028261, -0.051797, -0.115516, -0.066065, 0.045676, -0.056283, -0.019509, 0.05042, 0.081124, 0.038154, -0.014334, -0.058606, 0.03792, 0.027637, -0.002735, 0.016271, 0.01937, 0.018362, 0.029686, -0.002795, 0.022806, -0.081661, -0.022419, 0.059888, 0.027731, -0.015354, -0.048034, 0.004533, 0.024668, -0.044292, 0.015831, -0.027527, -0.090051, 0.031745, 0.003792, 0.042534, 0.02901, -0.050633, 0.020763, 0.037459, -0.02709, 0.040522, 0.020051, 0.082612, 0.012042, 0.007298, -0.067962, -0.022763, -0.021077, 0.034285, -0.020826, 0.033998, -0.05198, 0.063462, -0.022894, -0.000748, -0.010592, -0.045934, 0.060046, 0.087123, -0.074907, -0.071163, 0.00096, 0.023439, -0.036156, -0.046947, 0.038443, 0.040775, -0.027853, -0.014929, -0.057829, -0.020475, -0.042197, 0.010614, -0.02505, -0.067266, -0.07943, 0.070478, -0.033844, -0.092788, -0.019413, -0.04908, -0.024522, -0.033398, -0.036412, -0.05483, 0.008169, 3.1222e-33, 0.031554, 0.000771, 0.007574, 0.051644, 0.073467, -0.009317, 0.030396, -0.062817, -0.049469, 0.044147, 0.034081, 0.025187, -0.088969, -0.006547, 0.062778, -0.061899, -0.036726, 0.003266, -0.075354, -0.06239, -0.036646, -0.02648, 0.05139, -0.00278, 0.044895, 0.053791, -0.002873, -0.001813, 0.062168, 0.061544, -0.048848, -0.075151, 0.064802, 0.026789, -0.051642, 0.032601, -0.071421, -0.081286, 0.046636, 0.03493, 0.035291, 0.049091, 0.042534, 0.037599, -0.112757, 0.074343, 0.078106, 0.062747, 0.03701, -0.051521, 0.073341, 0.004294, -0.033282, -0.06057, -0.106117, 0.069402, 0.002812, -0.033351, 0.069086, 0.007338, 0.088768, 0.080587, 0.07143, -0.041803, 0.048226, 0.055912, 0.035525, -0.034257, -0.016601, 0.001304, 0.025647, 0.090811, 0.047174, 0.0274, 0.192374, -0.031598, 0.005798, 0.016118, -0.028017, -0.050514, -0.032982, -0.030666, -0.008585, 0.044641, 0.009376, -0.004384, 0.030215, 0.053329, 0.036001, 0.010282, -0.007498, -0.018606, -0.00543, 0.007872, -0.037521, -2.1325e-33, 0.045292, 0.034009, 0.079367, 0.013836, 0.129459, 0.015866, -0.012412, 0.073302, 0.026911, 0.039113, 0.136262, 0.010892, -0.100126, 0.020107, 0.083099, 0.086335, -0.010954, -0.078355, -0.127925, -0.081352, 0.017241, 0.003089, -0.037428, -0.00695, -0.091849, -0.012662, -0.008052, 0.114571, 0.024061, 0.023203, -0.055585, 0.03642, -0.010498, 0.046685, 0.037599, 0.036584, 0.060719, -0.018676, 0.005441, -0.007818, -0.034832, -0.062962, -0.025797, 0.061391, 0.013985, -0.02157, 0.031953, -0.03756, 0.10372, -0.010287, 0.017564, 0.018405, 0.074809, 0.162787, -0.061577, 0.020899, -0.024804, 0.049828, 0.006541, -0.041677, 0.056903, -0.016523, -0.034499, 0.00898, 0.050464, -0.043226, 0.0234, -0.118519, -0.117186, 0.060579, 0.074162, -0.047149, -0.006267, -0.002142, 0.025712, -0.031146, -0.045877, 0.087351, 0.019622, 0.024995, -0.007961, 0.007968, 0.014224, 0.010567, 0.117345, 0.057731, 0.017271, 0.033379, -0.038631, 0.052585, -0.067995, -0.007751, -0.018212, 0.060533, 0.034478, -1.7857e-08, 0.024968, 0.059021, 0.025095, -0.013359, 0.015754, 0.011613, -0.102571, -0.026736, 0.088794, -0.016668, 0.069948, 0.026237, 0.002091, -0.062308, -0.118881, -0.05263, -0.073112, 0.049371, 0.012477, -0.056494, 0.040603, 0.031014, 0.028105, -0.056266, -0.006609, 0.017164, -0.029324, 0.039515, -0.033209, -0.039764, 0.039809, 0.002515, 0.049539, -0.066961, 0.018707, -0.028472, -0.078797, 0.02635, 0.039791, -0.044775, 0.05594, -0.104986, -0.006516, -0.006211, 0.010965, 0.065731, 0.005421, 0.003755, -0.083055, 0.035492, 0.016322, -0.076782, -0.047287, 0.020692, -0.056092, -0.083851, -0.020714, 0.029802, -0.058987, 0.008525, -0.012308, -0.062131, 0.006247, -0.017416], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, 86d234a4-6b97-476c-ada8-efb344d39743);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH915XL','DataStax Vintage 2015 MVP Hoodie','DSH915',{'dsh915.jpg'},[-0.130689, 0.119207, 0.002184, 0.011354, 0.01946, -0.025444, 0.051016, 0.003948, -0.021649, -0.008893, 0.03938, -0.023064, 0.074983, -0.056983, 0.021864, -0.013808, 0.003896, -0.043946, 0.00557, -0.065687, -0.048401, -0.130076, -0.033392, 0.033522, -0.029537, 0.071937, -0.026538, 0.024381, -0.017099, -0.035164, 0.022241, -0.067904, 0.007624, 0.050735, -0.000709, -0.062166, 0.03406, 0.038965, -0.075732, 0.043013, -0.028261, -0.051797, -0.115516, -0.066065, 0.045676, -0.056283, -0.019509, 0.05042, 0.081124, 0.038154, -0.014334, -0.058606, 0.03792, 0.027637, -0.002735, 0.016271, 0.01937, 0.018362, 0.029686, -0.002795, 0.022806, -0.081661, -0.022419, 0.059888, 0.027731, -0.015354, -0.048034, 0.004533, 0.024668, -0.044292, 0.015831, -0.027527, -0.090051, 0.031745, 0.003792, 0.042534, 0.02901, -0.050633, 0.020763, 0.037459, -0.02709, 0.040522, 0.020051, 0.082612, 0.012042, 0.007298, -0.067962, -0.022763, -0.021077, 0.034285, -0.020826, 0.033998, -0.05198, 0.063462, -0.022894, -0.000748, -0.010592, -0.045934, 0.060046, 0.087123, -0.074907, -0.071163, 0.00096, 0.023439, -0.036156, -0.046947, 0.038443, 0.040775, -0.027853, -0.014929, -0.057829, -0.020475, -0.042197, 0.010614, -0.02505, -0.067266, -0.07943, 0.070478, -0.033844, -0.092788, -0.019413, -0.04908, -0.024522, -0.033398, -0.036412, -0.05483, 0.008169, 3.1222e-33, 0.031554, 0.000771, 0.007574, 0.051644, 0.073467, -0.009317, 0.030396, -0.062817, -0.049469, 0.044147, 0.034081, 0.025187, -0.088969, -0.006547, 0.062778, -0.061899, -0.036726, 0.003266, -0.075354, -0.06239, -0.036646, -0.02648, 0.05139, -0.00278, 0.044895, 0.053791, -0.002873, -0.001813, 0.062168, 0.061544, -0.048848, -0.075151, 0.064802, 0.026789, -0.051642, 0.032601, -0.071421, -0.081286, 0.046636, 0.03493, 0.035291, 0.049091, 0.042534, 0.037599, -0.112757, 0.074343, 0.078106, 0.062747, 0.03701, -0.051521, 0.073341, 0.004294, -0.033282, -0.06057, -0.106117, 0.069402, 0.002812, -0.033351, 0.069086, 0.007338, 0.088768, 0.080587, 0.07143, -0.041803, 0.048226, 0.055912, 0.035525, -0.034257, -0.016601, 0.001304, 0.025647, 0.090811, 0.047174, 0.0274, 0.192374, -0.031598, 0.005798, 0.016118, -0.028017, -0.050514, -0.032982, -0.030666, -0.008585, 0.044641, 0.009376, -0.004384, 0.030215, 0.053329, 0.036001, 0.010282, -0.007498, -0.018606, -0.00543, 0.007872, -0.037521, -2.1325e-33, 0.045292, 0.034009, 0.079367, 0.013836, 0.129459, 0.015866, -0.012412, 0.073302, 0.026911, 0.039113, 0.136262, 0.010892, -0.100126, 0.020107, 0.083099, 0.086335, -0.010954, -0.078355, -0.127925, -0.081352, 0.017241, 0.003089, -0.037428, -0.00695, -0.091849, -0.012662, -0.008052, 0.114571, 0.024061, 0.023203, -0.055585, 0.03642, -0.010498, 0.046685, 0.037599, 0.036584, 0.060719, -0.018676, 0.005441, -0.007818, -0.034832, -0.062962, -0.025797, 0.061391, 0.013985, -0.02157, 0.031953, -0.03756, 0.10372, -0.010287, 0.017564, 0.018405, 0.074809, 0.162787, -0.061577, 0.020899, -0.024804, 0.049828, 0.006541, -0.041677, 0.056903, -0.016523, -0.034499, 0.00898, 0.050464, -0.043226, 0.0234, -0.118519, -0.117186, 0.060579, 0.074162, -0.047149, -0.006267, -0.002142, 0.025712, -0.031146, -0.045877, 0.087351, 0.019622, 0.024995, -0.007961, 0.007968, 0.014224, 0.010567, 0.117345, 0.057731, 0.017271, 0.033379, -0.038631, 0.052585, -0.067995, -0.007751, -0.018212, 0.060533, 0.034478, -1.7857e-08, 0.024968, 0.059021, 0.025095, -0.013359, 0.015754, 0.011613, -0.102571, -0.026736, 0.088794, -0.016668, 0.069948, 0.026237, 0.002091, -0.062308, -0.118881, -0.05263, -0.073112, 0.049371, 0.012477, -0.056494, 0.040603, 0.031014, 0.028105, -0.056266, -0.006609, 0.017164, -0.029324, 0.039515, -0.033209, -0.039764, 0.039809, 0.002515, 0.049539, -0.066961, 0.018707, -0.028472, -0.078797, 0.02635, 0.039791, -0.044775, 0.05594, -0.104986, -0.006516, -0.006211, 0.010965, 0.065731, 0.005421, 0.003755, -0.083055, 0.035492, 0.016322, -0.076782, -0.047287, 0.020692, -0.056092, -0.083851, -0.020714, 0.029802, -0.058987, 0.008525, -0.012308, -0.062131, 0.006247, -0.017416], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, 86d234a4-6b97-476c-ada8-efb344d39743);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH916L','DataStax Black Hoodie','DSH916',{'dsh916.jpg'},[-0.130689, 0.119207, 0.002184, 0.011354, 0.01946, -0.025444, 0.051016, 0.003948, -0.021649, -0.008893, 0.03938, -0.023064, 0.074983, -0.056983, 0.021864, -0.013808, 0.003896, -0.043946, 0.00557, -0.065687, -0.048401, -0.130076, -0.033392, 0.033522, -0.029537, 0.071937, -0.026538, 0.024381, -0.017099, -0.035164, 0.022241, -0.067904, 0.007624, 0.050735, -0.000709, -0.062166, 0.03406, 0.038965, -0.075732, 0.043013, -0.028261, -0.051797, -0.115516, -0.066065, 0.045676, -0.056283, -0.019509, 0.05042, 0.081124, 0.038154, -0.014334, -0.058606, 0.03792, 0.027637, -0.002735, 0.016271, 0.01937, 0.018362, 0.029686, -0.002795, 0.022806, -0.081661, -0.022419, 0.059888, 0.027731, -0.015354, -0.048034, 0.004533, 0.024668, -0.044292, 0.015831, -0.027527, -0.090051, 0.031745, 0.003792, 0.042534, 0.02901, -0.050633, 0.020763, 0.037459, -0.02709, 0.040522, 0.020051, 0.082612, 0.012042, 0.007298, -0.067962, -0.022763, -0.021077, 0.034285, -0.020826, 0.033998, -0.05198, 0.063462, -0.022894, -0.000748, -0.010592, -0.045934, 0.060046, 0.087123, -0.074907, -0.071163, 0.00096, 0.023439, -0.036156, -0.046947, 0.038443, 0.040775, -0.027853, -0.014929, -0.057829, -0.020475, -0.042197, 0.010614, -0.02505, -0.067266, -0.07943, 0.070478, -0.033844, -0.092788, -0.019413, -0.04908, -0.024522, -0.033398, -0.036412, -0.05483, 0.008169, 3.1222e-33, 0.031554, 0.000771, 0.007574, 0.051644, 0.073467, -0.009317, 0.030396, -0.062817, -0.049469, 0.044147, 0.034081, 0.025187, -0.088969, -0.006547, 0.062778, -0.061899, -0.036726, 0.003266, -0.075354, -0.06239, -0.036646, -0.02648, 0.05139, -0.00278, 0.044895, 0.053791, -0.002873, -0.001813, 0.062168, 0.061544, -0.048848, -0.075151, 0.064802, 0.026789, -0.051642, 0.032601, -0.071421, -0.081286, 0.046636, 0.03493, 0.035291, 0.049091, 0.042534, 0.037599, -0.112757, 0.074343, 0.078106, 0.062747, 0.03701, -0.051521, 0.073341, 0.004294, -0.033282, -0.06057, -0.106117, 0.069402, 0.002812, -0.033351, 0.069086, 0.007338, 0.088768, 0.080587, 0.07143, -0.041803, 0.048226, 0.055912, 0.035525, -0.034257, -0.016601, 0.001304, 0.025647, 0.090811, 0.047174, 0.0274, 0.192374, -0.031598, 0.005798, 0.016118, -0.028017, -0.050514, -0.032982, -0.030666, -0.008585, 0.044641, 0.009376, -0.004384, 0.030215, 0.053329, 0.036001, 0.010282, -0.007498, -0.018606, -0.00543, 0.007872, -0.037521, -2.1325e-33, 0.045292, 0.034009, 0.079367, 0.013836, 0.129459, 0.015866, -0.012412, 0.073302, 0.026911, 0.039113, 0.136262, 0.010892, -0.100126, 0.020107, 0.083099, 0.086335, -0.010954, -0.078355, -0.127925, -0.081352, 0.017241, 0.003089, -0.037428, -0.00695, -0.091849, -0.012662, -0.008052, 0.114571, 0.024061, 0.023203, -0.055585, 0.03642, -0.010498, 0.046685, 0.037599, 0.036584, 0.060719, -0.018676, 0.005441, -0.007818, -0.034832, -0.062962, -0.025797, 0.061391, 0.013985, -0.02157, 0.031953, -0.03756, 0.10372, -0.010287, 0.017564, 0.018405, 0.074809, 0.162787, -0.061577, 0.020899, -0.024804, 0.049828, 0.006541, -0.041677, 0.056903, -0.016523, -0.034499, 0.00898, 0.050464, -0.043226, 0.0234, -0.118519, -0.117186, 0.060579, 0.074162, -0.047149, -0.006267, -0.002142, 0.025712, -0.031146, -0.045877, 0.087351, 0.019622, 0.024995, -0.007961, 0.007968, 0.014224, 0.010567, 0.117345, 0.057731, 0.017271, 0.033379, -0.038631, 0.052585, -0.067995, -0.007751, -0.018212, 0.060533, 0.034478, -1.7857e-08, 0.024968, 0.059021, 0.025095, -0.013359, 0.015754, 0.011613, -0.102571, -0.026736, 0.088794, -0.016668, 0.069948, 0.026237, 0.002091, -0.062308, -0.118881, -0.05263, -0.073112, 0.049371, 0.012477, -0.056494, 0.040603, 0.031014, 0.028105, -0.056266, -0.006609, 0.017164, -0.029324, 0.039515, -0.033209, -0.039764, 0.039809, 0.002515, 0.049539, -0.066961, 0.018707, -0.028472, -0.078797, 0.02635, 0.039791, -0.044775, 0.05594, -0.104986, -0.006516, -0.006211, 0.010965, 0.065731, 0.005421, 0.003755, -0.083055, 0.035492, 0.016322, -0.076782, -0.047287, 0.020692, -0.056092, -0.083851, -0.020714, 0.029802, -0.058987, 0.008525, -0.012308, -0.062131, 0.006247, -0.017416], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, b9bed3c0-0a76-44ea-bce6-f5f21611a3f1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH916M','DataStax Black Hoodie','DSH916',{'dsh916.jpg'},[-0.130689, 0.119207, 0.002184, 0.011354, 0.01946, -0.025444, 0.051016, 0.003948, -0.021649, -0.008893, 0.03938, -0.023064, 0.074983, -0.056983, 0.021864, -0.013808, 0.003896, -0.043946, 0.00557, -0.065687, -0.048401, -0.130076, -0.033392, 0.033522, -0.029537, 0.071937, -0.026538, 0.024381, -0.017099, -0.035164, 0.022241, -0.067904, 0.007624, 0.050735, -0.000709, -0.062166, 0.03406, 0.038965, -0.075732, 0.043013, -0.028261, -0.051797, -0.115516, -0.066065, 0.045676, -0.056283, -0.019509, 0.05042, 0.081124, 0.038154, -0.014334, -0.058606, 0.03792, 0.027637, -0.002735, 0.016271, 0.01937, 0.018362, 0.029686, -0.002795, 0.022806, -0.081661, -0.022419, 0.059888, 0.027731, -0.015354, -0.048034, 0.004533, 0.024668, -0.044292, 0.015831, -0.027527, -0.090051, 0.031745, 0.003792, 0.042534, 0.02901, -0.050633, 0.020763, 0.037459, -0.02709, 0.040522, 0.020051, 0.082612, 0.012042, 0.007298, -0.067962, -0.022763, -0.021077, 0.034285, -0.020826, 0.033998, -0.05198, 0.063462, -0.022894, -0.000748, -0.010592, -0.045934, 0.060046, 0.087123, -0.074907, -0.071163, 0.00096, 0.023439, -0.036156, -0.046947, 0.038443, 0.040775, -0.027853, -0.014929, -0.057829, -0.020475, -0.042197, 0.010614, -0.02505, -0.067266, -0.07943, 0.070478, -0.033844, -0.092788, -0.019413, -0.04908, -0.024522, -0.033398, -0.036412, -0.05483, 0.008169, 3.1222e-33, 0.031554, 0.000771, 0.007574, 0.051644, 0.073467, -0.009317, 0.030396, -0.062817, -0.049469, 0.044147, 0.034081, 0.025187, -0.088969, -0.006547, 0.062778, -0.061899, -0.036726, 0.003266, -0.075354, -0.06239, -0.036646, -0.02648, 0.05139, -0.00278, 0.044895, 0.053791, -0.002873, -0.001813, 0.062168, 0.061544, -0.048848, -0.075151, 0.064802, 0.026789, -0.051642, 0.032601, -0.071421, -0.081286, 0.046636, 0.03493, 0.035291, 0.049091, 0.042534, 0.037599, -0.112757, 0.074343, 0.078106, 0.062747, 0.03701, -0.051521, 0.073341, 0.004294, -0.033282, -0.06057, -0.106117, 0.069402, 0.002812, -0.033351, 0.069086, 0.007338, 0.088768, 0.080587, 0.07143, -0.041803, 0.048226, 0.055912, 0.035525, -0.034257, -0.016601, 0.001304, 0.025647, 0.090811, 0.047174, 0.0274, 0.192374, -0.031598, 0.005798, 0.016118, -0.028017, -0.050514, -0.032982, -0.030666, -0.008585, 0.044641, 0.009376, -0.004384, 0.030215, 0.053329, 0.036001, 0.010282, -0.007498, -0.018606, -0.00543, 0.007872, -0.037521, -2.1325e-33, 0.045292, 0.034009, 0.079367, 0.013836, 0.129459, 0.015866, -0.012412, 0.073302, 0.026911, 0.039113, 0.136262, 0.010892, -0.100126, 0.020107, 0.083099, 0.086335, -0.010954, -0.078355, -0.127925, -0.081352, 0.017241, 0.003089, -0.037428, -0.00695, -0.091849, -0.012662, -0.008052, 0.114571, 0.024061, 0.023203, -0.055585, 0.03642, -0.010498, 0.046685, 0.037599, 0.036584, 0.060719, -0.018676, 0.005441, -0.007818, -0.034832, -0.062962, -0.025797, 0.061391, 0.013985, -0.02157, 0.031953, -0.03756, 0.10372, -0.010287, 0.017564, 0.018405, 0.074809, 0.162787, -0.061577, 0.020899, -0.024804, 0.049828, 0.006541, -0.041677, 0.056903, -0.016523, -0.034499, 0.00898, 0.050464, -0.043226, 0.0234, -0.118519, -0.117186, 0.060579, 0.074162, -0.047149, -0.006267, -0.002142, 0.025712, -0.031146, -0.045877, 0.087351, 0.019622, 0.024995, -0.007961, 0.007968, 0.014224, 0.010567, 0.117345, 0.057731, 0.017271, 0.033379, -0.038631, 0.052585, -0.067995, -0.007751, -0.018212, 0.060533, 0.034478, -1.7857e-08, 0.024968, 0.059021, 0.025095, -0.013359, 0.015754, 0.011613, -0.102571, -0.026736, 0.088794, -0.016668, 0.069948, 0.026237, 0.002091, -0.062308, -0.118881, -0.05263, -0.073112, 0.049371, 0.012477, -0.056494, 0.040603, 0.031014, 0.028105, -0.056266, -0.006609, 0.017164, -0.029324, 0.039515, -0.033209, -0.039764, 0.039809, 0.002515, 0.049539, -0.066961, 0.018707, -0.028472, -0.078797, 0.02635, 0.039791, -0.044775, 0.05594, -0.104986, -0.006516, -0.006211, 0.010965, 0.065731, 0.005421, 0.003755, -0.083055, 0.035492, 0.016322, -0.076782, -0.047287, 0.020692, -0.056092, -0.083851, -0.020714, 0.029802, -0.058987, 0.008525, -0.012308, -0.062131, 0.006247, -0.017416], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, b9bed3c0-0a76-44ea-bce6-f5f21611a3f1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('DSH916S','DataStax Black Hoodie','DSH916',{'dsh916.jpg'},[-0.130689, 0.119207, 0.002184, 0.011354, 0.01946, -0.025444, 0.051016, 0.003948, -0.021649, -0.008893, 0.03938, -0.023064, 0.074983, -0.056983, 0.021864, -0.013808, 0.003896, -0.043946, 0.00557, -0.065687, -0.048401, -0.130076, -0.033392, 0.033522, -0.029537, 0.071937, -0.026538, 0.024381, -0.017099, -0.035164, 0.022241, -0.067904, 0.007624, 0.050735, -0.000709, -0.062166, 0.03406, 0.038965, -0.075732, 0.043013, -0.028261, -0.051797, -0.115516, -0.066065, 0.045676, -0.056283, -0.019509, 0.05042, 0.081124, 0.038154, -0.014334, -0.058606, 0.03792, 0.027637, -0.002735, 0.016271, 0.01937, 0.018362, 0.029686, -0.002795, 0.022806, -0.081661, -0.022419, 0.059888, 0.027731, -0.015354, -0.048034, 0.004533, 0.024668, -0.044292, 0.015831, -0.027527, -0.090051, 0.031745, 0.003792, 0.042534, 0.02901, -0.050633, 0.020763, 0.037459, -0.02709, 0.040522, 0.020051, 0.082612, 0.012042, 0.007298, -0.067962, -0.022763, -0.021077, 0.034285, -0.020826, 0.033998, -0.05198, 0.063462, -0.022894, -0.000748, -0.010592, -0.045934, 0.060046, 0.087123, -0.074907, -0.071163, 0.00096, 0.023439, -0.036156, -0.046947, 0.038443, 0.040775, -0.027853, -0.014929, -0.057829, -0.020475, -0.042197, 0.010614, -0.02505, -0.067266, -0.07943, 0.070478, -0.033844, -0.092788, -0.019413, -0.04908, -0.024522, -0.033398, -0.036412, -0.05483, 0.008169, 3.1222e-33, 0.031554, 0.000771, 0.007574, 0.051644, 0.073467, -0.009317, 0.030396, -0.062817, -0.049469, 0.044147, 0.034081, 0.025187, -0.088969, -0.006547, 0.062778, -0.061899, -0.036726, 0.003266, -0.075354, -0.06239, -0.036646, -0.02648, 0.05139, -0.00278, 0.044895, 0.053791, -0.002873, -0.001813, 0.062168, 0.061544, -0.048848, -0.075151, 0.064802, 0.026789, -0.051642, 0.032601, -0.071421, -0.081286, 0.046636, 0.03493, 0.035291, 0.049091, 0.042534, 0.037599, -0.112757, 0.074343, 0.078106, 0.062747, 0.03701, -0.051521, 0.073341, 0.004294, -0.033282, -0.06057, -0.106117, 0.069402, 0.002812, -0.033351, 0.069086, 0.007338, 0.088768, 0.080587, 0.07143, -0.041803, 0.048226, 0.055912, 0.035525, -0.034257, -0.016601, 0.001304, 0.025647, 0.090811, 0.047174, 0.0274, 0.192374, -0.031598, 0.005798, 0.016118, -0.028017, -0.050514, -0.032982, -0.030666, -0.008585, 0.044641, 0.009376, -0.004384, 0.030215, 0.053329, 0.036001, 0.010282, -0.007498, -0.018606, -0.00543, 0.007872, -0.037521, -2.1325e-33, 0.045292, 0.034009, 0.079367, 0.013836, 0.129459, 0.015866, -0.012412, 0.073302, 0.026911, 0.039113, 0.136262, 0.010892, -0.100126, 0.020107, 0.083099, 0.086335, -0.010954, -0.078355, -0.127925, -0.081352, 0.017241, 0.003089, -0.037428, -0.00695, -0.091849, -0.012662, -0.008052, 0.114571, 0.024061, 0.023203, -0.055585, 0.03642, -0.010498, 0.046685, 0.037599, 0.036584, 0.060719, -0.018676, 0.005441, -0.007818, -0.034832, -0.062962, -0.025797, 0.061391, 0.013985, -0.02157, 0.031953, -0.03756, 0.10372, -0.010287, 0.017564, 0.018405, 0.074809, 0.162787, -0.061577, 0.020899, -0.024804, 0.049828, 0.006541, -0.041677, 0.056903, -0.016523, -0.034499, 0.00898, 0.050464, -0.043226, 0.0234, -0.118519, -0.117186, 0.060579, 0.074162, -0.047149, -0.006267, -0.002142, 0.025712, -0.031146, -0.045877, 0.087351, 0.019622, 0.024995, -0.007961, 0.007968, 0.014224, 0.010567, 0.117345, 0.057731, 0.017271, 0.033379, -0.038631, 0.052585, -0.067995, -0.007751, -0.018212, 0.060533, 0.034478, -1.7857e-08, 0.024968, 0.059021, 0.025095, -0.013359, 0.015754, 0.011613, -0.102571, -0.026736, 0.088794, -0.016668, 0.069948, 0.026237, 0.002091, -0.062308, -0.118881, -0.05263, -0.073112, 0.049371, 0.012477, -0.056494, 0.040603, 0.031014, 0.028105, -0.056266, -0.006609, 0.017164, -0.029324, 0.039515, -0.033209, -0.039764, 0.039809, 0.002515, 0.049539, -0.066961, 0.018707, -0.028472, -0.078797, 0.02635, 0.039791, -0.044775, 0.05594, -0.104986, -0.006516, -0.006211, 0.010965, 0.065731, 0.005421, 0.003755, -0.083055, 0.035492, 0.016322, -0.076782, -0.047287, 0.020692, -0.056092, -0.083851, -0.020714, 0.029802, -0.058987, 0.008525, -0.012308, -0.062131, 0.006247, -0.017416], 6a4d86aa-ceb5-4c6f-b9b9-80e9a8c58ad1, b9bed3c0-0a76-44ea-bce6-f5f21611a3f1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LS5342XL','Go Away Annotation T-Shirt','LS534',{'ls534.png'},[-0.036608, 0.17069, -6.8e-05, 0.013803, 0.155039, 0.032511, 0.115352, -0.017075, 0.02312, -0.053608, 0.036347, 0.00325, 0.018615, 0.045028, 0.024062, 0.053021, 0.016931, 0.014446, -0.035558, -0.055052, -0.023493, 0.055527, 0.062397, 0.077756, -0.011309, -0.055835, -0.052177, -0.028795, -0.014989, -0.009282, -0.03812, 0.089499, -0.003033, 0.005257, -0.030212, -0.039765, 0.019952, 0.104384, 0.023942, 0.028156, 0.000343, -0.096789, -0.067907, 0.030879, 0.037792, 0.091158, -0.006283, 0.014304, -0.063456, 0.096292, 0.030419, -0.083409, -0.010061, -0.054874, 0.01327, 0.035466, 0.094629, -0.042095, -0.05736, 0.024775, 0.080676, -0.052107, -0.076339, 0.061823, 0.078378, -0.000108, 0.046956, 0.038856, -0.026526, 0.017161, 0.059612, -0.018435, 0.026958, 0.102243, 0.039013, 0.071186, 0.033843, -0.017657, 0.03598, -0.024247, -0.056728, 0.045184, 0.054087, -0.025071, -0.048991, 0.02662, -0.016531, -0.009302, 0.046526, -0.003961, -0.049663, 0.007909, 0.052308, 0.017191, -0.021396, -0.023883, -0.12157, -0.015862, -0.063323, 0.119512, 0.019032, 0.037145, 0.031725, 0.037319, 0.011787, -0.061692, -0.006838, 0.010912, 0.064413, -0.000109, -0.021548, 0.022913, -0.022013, -0.005394, -0.007294, -0.034773, 0.085055, 0.09958, 0.084548, -0.029302, -0.028561, 0.01266, -0.079814, 0.024768, 0.015278, -0.038034, -0.021359, -8.1697e-35, 0.073428, 0.055485, -0.044437, 0.075282, 0.02274, -0.023128, -0.040903, -0.116771, 0.016573, -0.003524, 0.030685, -0.045409, -0.019008, 0.042953, -0.006942, -0.000543, -0.00102, -0.014377, -0.048558, -0.054464, -0.004539, 0.111372, -0.002005, -0.060866, -0.022815, 0.10073, 0.008343, -0.069105, -0.03674, 0.031958, 0.045063, -0.013123, 0.047586, 0.007698, -0.003711, -0.048613, -0.033931, 0.002353, 0.030301, -0.001543, 0.064156, 0.046028, -0.001938, 0.010193, 0.013444, 0.002869, 0.081806, 0.012772, 0.014697, -0.014563, 0.065606, -0.010437, -0.012719, -0.130164, -0.088925, -0.078527, -0.007429, 0.009874, 0.02923, -0.073438, -0.004822, 0.034371, 0.065097, -0.003957, -0.019316, 0.000311, -0.058143, -0.042109, -0.020749, -0.089716, -0.041534, 0.049955, -0.001552, -0.021463, -0.021696, -0.079449, 0.065888, -0.034232, 0.012767, -0.033467, -0.043992, -0.04138, -0.012641, -0.015642, 0.005837, -0.055145, 0.071815, -0.067067, 0.02759, 0.012492, 0.000179, 0.047313, -0.068053, -0.007179, -0.010233, -5.4606e-34, 0.025166, 0.034118, -0.024733, 0.047228, 0.003747, -0.070258, 0.006291, 0.152883, -0.036288, 0.040884, 0.03752, -0.066384, -0.109309, -0.053935, 0.051464, 0.013807, 0.092907, 0.094678, -0.09784, 0.016447, -0.061126, -0.06691, -0.066924, -0.012761, -0.011371, 0.042607, 0.07928, -0.032157, 0.001585, -0.10567, -0.076343, -0.028213, 0.052881, 0.011874, 0.040783, -0.024232, 0.019623, -0.009142, -0.017977, 0.011064, 0.063624, 0.007392, -0.048477, 0.027756, -0.083771, -0.087622, -0.10184, -0.039955, -0.040235, 0.06248, -0.033642, -0.056431, 0.062244, -0.076749, -0.074775, 0.01314, 0.028267, 0.00843, -0.05746, 0.035314, -0.014488, 0.017557, -0.065159, 0.017372, 0.076326, -0.023451, -0.064457, 0.051611, -0.089624, 0.044742, 0.030695, 0.037951, -0.030822, -0.086775, -0.046732, -0.077627, 0.065684, 0.02859, -0.032896, -0.045175, -0.029318, -0.050483, -0.017911, 0.067584, 0.076418, 0.007941, -0.043728, 0.021922, 0.003156, 0.045067, 0.016599, 0.031952, 0.063431, 0.118829, 0.027209, -2.0099e-08, -0.030052, -0.020226, 0.02799, 0.033707, 0.045369, 0.044223, -0.004201, 0.02209, 0.005018, 0.044784, -0.00522, 0.009344, -0.086125, -0.030412, 0.001503, -0.075738, -0.027691, -0.012998, -0.096883, 0.009345, -0.098639, -0.012999, -0.014733, -0.023915, -0.001093, 0.031345, -0.010145, 0.144356, 0.050369, -0.013375, -0.024485, 0.033877, -0.05207, -0.002538, -0.037734, 0.02646, 0.039215, -0.059336, 0.036362, 0.020788, -0.054643, 0.002337, -0.001766, 0.045326, 0.100353, -0.065405, -0.001118, -0.030746, -0.058903, -0.058318, -0.022108, -0.031307, -0.073587, 0.097973, -0.027214, -0.044981, 0.05254, 0.042965, 0.002071, 0.074197, 0.003986, -0.068829, -0.012316, -0.061178], 91455473-212e-4c6e-8bec-1da06779ae10, 99c4d825-d262-4a95-a04e-cc72e7e273c1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LS5343XL','Go Away Annotation T-Shirt','LS534',{'ls534.png'},[-0.036608, 0.17069, -6.8e-05, 0.013803, 0.155039, 0.032511, 0.115352, -0.017075, 0.02312, -0.053608, 0.036347, 0.00325, 0.018615, 0.045028, 0.024062, 0.053021, 0.016931, 0.014446, -0.035558, -0.055052, -0.023493, 0.055527, 0.062397, 0.077756, -0.011309, -0.055835, -0.052177, -0.028795, -0.014989, -0.009282, -0.03812, 0.089499, -0.003033, 0.005257, -0.030212, -0.039765, 0.019952, 0.104384, 0.023942, 0.028156, 0.000343, -0.096789, -0.067907, 0.030879, 0.037792, 0.091158, -0.006283, 0.014304, -0.063456, 0.096292, 0.030419, -0.083409, -0.010061, -0.054874, 0.01327, 0.035466, 0.094629, -0.042095, -0.05736, 0.024775, 0.080676, -0.052107, -0.076339, 0.061823, 0.078378, -0.000108, 0.046956, 0.038856, -0.026526, 0.017161, 0.059612, -0.018435, 0.026958, 0.102243, 0.039013, 0.071186, 0.033843, -0.017657, 0.03598, -0.024247, -0.056728, 0.045184, 0.054087, -0.025071, -0.048991, 0.02662, -0.016531, -0.009302, 0.046526, -0.003961, -0.049663, 0.007909, 0.052308, 0.017191, -0.021396, -0.023883, -0.12157, -0.015862, -0.063323, 0.119512, 0.019032, 0.037145, 0.031725, 0.037319, 0.011787, -0.061692, -0.006838, 0.010912, 0.064413, -0.000109, -0.021548, 0.022913, -0.022013, -0.005394, -0.007294, -0.034773, 0.085055, 0.09958, 0.084548, -0.029302, -0.028561, 0.01266, -0.079814, 0.024768, 0.015278, -0.038034, -0.021359, -8.1697e-35, 0.073428, 0.055485, -0.044437, 0.075282, 0.02274, -0.023128, -0.040903, -0.116771, 0.016573, -0.003524, 0.030685, -0.045409, -0.019008, 0.042953, -0.006942, -0.000543, -0.00102, -0.014377, -0.048558, -0.054464, -0.004539, 0.111372, -0.002005, -0.060866, -0.022815, 0.10073, 0.008343, -0.069105, -0.03674, 0.031958, 0.045063, -0.013123, 0.047586, 0.007698, -0.003711, -0.048613, -0.033931, 0.002353, 0.030301, -0.001543, 0.064156, 0.046028, -0.001938, 0.010193, 0.013444, 0.002869, 0.081806, 0.012772, 0.014697, -0.014563, 0.065606, -0.010437, -0.012719, -0.130164, -0.088925, -0.078527, -0.007429, 0.009874, 0.02923, -0.073438, -0.004822, 0.034371, 0.065097, -0.003957, -0.019316, 0.000311, -0.058143, -0.042109, -0.020749, -0.089716, -0.041534, 0.049955, -0.001552, -0.021463, -0.021696, -0.079449, 0.065888, -0.034232, 0.012767, -0.033467, -0.043992, -0.04138, -0.012641, -0.015642, 0.005837, -0.055145, 0.071815, -0.067067, 0.02759, 0.012492, 0.000179, 0.047313, -0.068053, -0.007179, -0.010233, -5.4606e-34, 0.025166, 0.034118, -0.024733, 0.047228, 0.003747, -0.070258, 0.006291, 0.152883, -0.036288, 0.040884, 0.03752, -0.066384, -0.109309, -0.053935, 0.051464, 0.013807, 0.092907, 0.094678, -0.09784, 0.016447, -0.061126, -0.06691, -0.066924, -0.012761, -0.011371, 0.042607, 0.07928, -0.032157, 0.001585, -0.10567, -0.076343, -0.028213, 0.052881, 0.011874, 0.040783, -0.024232, 0.019623, -0.009142, -0.017977, 0.011064, 0.063624, 0.007392, -0.048477, 0.027756, -0.083771, -0.087622, -0.10184, -0.039955, -0.040235, 0.06248, -0.033642, -0.056431, 0.062244, -0.076749, -0.074775, 0.01314, 0.028267, 0.00843, -0.05746, 0.035314, -0.014488, 0.017557, -0.065159, 0.017372, 0.076326, -0.023451, -0.064457, 0.051611, -0.089624, 0.044742, 0.030695, 0.037951, -0.030822, -0.086775, -0.046732, -0.077627, 0.065684, 0.02859, -0.032896, -0.045175, -0.029318, -0.050483, -0.017911, 0.067584, 0.076418, 0.007941, -0.043728, 0.021922, 0.003156, 0.045067, 0.016599, 0.031952, 0.063431, 0.118829, 0.027209, -2.0099e-08, -0.030052, -0.020226, 0.02799, 0.033707, 0.045369, 0.044223, -0.004201, 0.02209, 0.005018, 0.044784, -0.00522, 0.009344, -0.086125, -0.030412, 0.001503, -0.075738, -0.027691, -0.012998, -0.096883, 0.009345, -0.098639, -0.012999, -0.014733, -0.023915, -0.001093, 0.031345, -0.010145, 0.144356, 0.050369, -0.013375, -0.024485, 0.033877, -0.05207, -0.002538, -0.037734, 0.02646, 0.039215, -0.059336, 0.036362, 0.020788, -0.054643, 0.002337, -0.001766, 0.045326, 0.100353, -0.065405, -0.001118, -0.030746, -0.058903, -0.058318, -0.022108, -0.031307, -0.073587, 0.097973, -0.027214, -0.044981, 0.05254, 0.042965, 0.002071, 0.074197, 0.003986, -0.068829, -0.012316, -0.061178], 91455473-212e-4c6e-8bec-1da06779ae10, 99c4d825-d262-4a95-a04e-cc72e7e273c1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LS534L','Go Away Annotation T-Shirt','LS534',{'ls534.png'},[-0.036608, 0.17069, -6.8e-05, 0.013803, 0.155039, 0.032511, 0.115352, -0.017075, 0.02312, -0.053608, 0.036347, 0.00325, 0.018615, 0.045028, 0.024062, 0.053021, 0.016931, 0.014446, -0.035558, -0.055052, -0.023493, 0.055527, 0.062397, 0.077756, -0.011309, -0.055835, -0.052177, -0.028795, -0.014989, -0.009282, -0.03812, 0.089499, -0.003033, 0.005257, -0.030212, -0.039765, 0.019952, 0.104384, 0.023942, 0.028156, 0.000343, -0.096789, -0.067907, 0.030879, 0.037792, 0.091158, -0.006283, 0.014304, -0.063456, 0.096292, 0.030419, -0.083409, -0.010061, -0.054874, 0.01327, 0.035466, 0.094629, -0.042095, -0.05736, 0.024775, 0.080676, -0.052107, -0.076339, 0.061823, 0.078378, -0.000108, 0.046956, 0.038856, -0.026526, 0.017161, 0.059612, -0.018435, 0.026958, 0.102243, 0.039013, 0.071186, 0.033843, -0.017657, 0.03598, -0.024247, -0.056728, 0.045184, 0.054087, -0.025071, -0.048991, 0.02662, -0.016531, -0.009302, 0.046526, -0.003961, -0.049663, 0.007909, 0.052308, 0.017191, -0.021396, -0.023883, -0.12157, -0.015862, -0.063323, 0.119512, 0.019032, 0.037145, 0.031725, 0.037319, 0.011787, -0.061692, -0.006838, 0.010912, 0.064413, -0.000109, -0.021548, 0.022913, -0.022013, -0.005394, -0.007294, -0.034773, 0.085055, 0.09958, 0.084548, -0.029302, -0.028561, 0.01266, -0.079814, 0.024768, 0.015278, -0.038034, -0.021359, -8.1697e-35, 0.073428, 0.055485, -0.044437, 0.075282, 0.02274, -0.023128, -0.040903, -0.116771, 0.016573, -0.003524, 0.030685, -0.045409, -0.019008, 0.042953, -0.006942, -0.000543, -0.00102, -0.014377, -0.048558, -0.054464, -0.004539, 0.111372, -0.002005, -0.060866, -0.022815, 0.10073, 0.008343, -0.069105, -0.03674, 0.031958, 0.045063, -0.013123, 0.047586, 0.007698, -0.003711, -0.048613, -0.033931, 0.002353, 0.030301, -0.001543, 0.064156, 0.046028, -0.001938, 0.010193, 0.013444, 0.002869, 0.081806, 0.012772, 0.014697, -0.014563, 0.065606, -0.010437, -0.012719, -0.130164, -0.088925, -0.078527, -0.007429, 0.009874, 0.02923, -0.073438, -0.004822, 0.034371, 0.065097, -0.003957, -0.019316, 0.000311, -0.058143, -0.042109, -0.020749, -0.089716, -0.041534, 0.049955, -0.001552, -0.021463, -0.021696, -0.079449, 0.065888, -0.034232, 0.012767, -0.033467, -0.043992, -0.04138, -0.012641, -0.015642, 0.005837, -0.055145, 0.071815, -0.067067, 0.02759, 0.012492, 0.000179, 0.047313, -0.068053, -0.007179, -0.010233, -5.4606e-34, 0.025166, 0.034118, -0.024733, 0.047228, 0.003747, -0.070258, 0.006291, 0.152883, -0.036288, 0.040884, 0.03752, -0.066384, -0.109309, -0.053935, 0.051464, 0.013807, 0.092907, 0.094678, -0.09784, 0.016447, -0.061126, -0.06691, -0.066924, -0.012761, -0.011371, 0.042607, 0.07928, -0.032157, 0.001585, -0.10567, -0.076343, -0.028213, 0.052881, 0.011874, 0.040783, -0.024232, 0.019623, -0.009142, -0.017977, 0.011064, 0.063624, 0.007392, -0.048477, 0.027756, -0.083771, -0.087622, -0.10184, -0.039955, -0.040235, 0.06248, -0.033642, -0.056431, 0.062244, -0.076749, -0.074775, 0.01314, 0.028267, 0.00843, -0.05746, 0.035314, -0.014488, 0.017557, -0.065159, 0.017372, 0.076326, -0.023451, -0.064457, 0.051611, -0.089624, 0.044742, 0.030695, 0.037951, -0.030822, -0.086775, -0.046732, -0.077627, 0.065684, 0.02859, -0.032896, -0.045175, -0.029318, -0.050483, -0.017911, 0.067584, 0.076418, 0.007941, -0.043728, 0.021922, 0.003156, 0.045067, 0.016599, 0.031952, 0.063431, 0.118829, 0.027209, -2.0099e-08, -0.030052, -0.020226, 0.02799, 0.033707, 0.045369, 0.044223, -0.004201, 0.02209, 0.005018, 0.044784, -0.00522, 0.009344, -0.086125, -0.030412, 0.001503, -0.075738, -0.027691, -0.012998, -0.096883, 0.009345, -0.098639, -0.012999, -0.014733, -0.023915, -0.001093, 0.031345, -0.010145, 0.144356, 0.050369, -0.013375, -0.024485, 0.033877, -0.05207, -0.002538, -0.037734, 0.02646, 0.039215, -0.059336, 0.036362, 0.020788, -0.054643, 0.002337, -0.001766, 0.045326, 0.100353, -0.065405, -0.001118, -0.030746, -0.058903, -0.058318, -0.022108, -0.031307, -0.073587, 0.097973, -0.027214, -0.044981, 0.05254, 0.042965, 0.002071, 0.074197, 0.003986, -0.068829, -0.012316, -0.061178], 91455473-212e-4c6e-8bec-1da06779ae10, 99c4d825-d262-4a95-a04e-cc72e7e273c1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LS534M','Go Away Annotation T-Shirt','LS534',{'ls534.png'},[-0.036608, 0.17069, -6.8e-05, 0.013803, 0.155039, 0.032511, 0.115352, -0.017075, 0.02312, -0.053608, 0.036347, 0.00325, 0.018615, 0.045028, 0.024062, 0.053021, 0.016931, 0.014446, -0.035558, -0.055052, -0.023493, 0.055527, 0.062397, 0.077756, -0.011309, -0.055835, -0.052177, -0.028795, -0.014989, -0.009282, -0.03812, 0.089499, -0.003033, 0.005257, -0.030212, -0.039765, 0.019952, 0.104384, 0.023942, 0.028156, 0.000343, -0.096789, -0.067907, 0.030879, 0.037792, 0.091158, -0.006283, 0.014304, -0.063456, 0.096292, 0.030419, -0.083409, -0.010061, -0.054874, 0.01327, 0.035466, 0.094629, -0.042095, -0.05736, 0.024775, 0.080676, -0.052107, -0.076339, 0.061823, 0.078378, -0.000108, 0.046956, 0.038856, -0.026526, 0.017161, 0.059612, -0.018435, 0.026958, 0.102243, 0.039013, 0.071186, 0.033843, -0.017657, 0.03598, -0.024247, -0.056728, 0.045184, 0.054087, -0.025071, -0.048991, 0.02662, -0.016531, -0.009302, 0.046526, -0.003961, -0.049663, 0.007909, 0.052308, 0.017191, -0.021396, -0.023883, -0.12157, -0.015862, -0.063323, 0.119512, 0.019032, 0.037145, 0.031725, 0.037319, 0.011787, -0.061692, -0.006838, 0.010912, 0.064413, -0.000109, -0.021548, 0.022913, -0.022013, -0.005394, -0.007294, -0.034773, 0.085055, 0.09958, 0.084548, -0.029302, -0.028561, 0.01266, -0.079814, 0.024768, 0.015278, -0.038034, -0.021359, -8.1697e-35, 0.073428, 0.055485, -0.044437, 0.075282, 0.02274, -0.023128, -0.040903, -0.116771, 0.016573, -0.003524, 0.030685, -0.045409, -0.019008, 0.042953, -0.006942, -0.000543, -0.00102, -0.014377, -0.048558, -0.054464, -0.004539, 0.111372, -0.002005, -0.060866, -0.022815, 0.10073, 0.008343, -0.069105, -0.03674, 0.031958, 0.045063, -0.013123, 0.047586, 0.007698, -0.003711, -0.048613, -0.033931, 0.002353, 0.030301, -0.001543, 0.064156, 0.046028, -0.001938, 0.010193, 0.013444, 0.002869, 0.081806, 0.012772, 0.014697, -0.014563, 0.065606, -0.010437, -0.012719, -0.130164, -0.088925, -0.078527, -0.007429, 0.009874, 0.02923, -0.073438, -0.004822, 0.034371, 0.065097, -0.003957, -0.019316, 0.000311, -0.058143, -0.042109, -0.020749, -0.089716, -0.041534, 0.049955, -0.001552, -0.021463, -0.021696, -0.079449, 0.065888, -0.034232, 0.012767, -0.033467, -0.043992, -0.04138, -0.012641, -0.015642, 0.005837, -0.055145, 0.071815, -0.067067, 0.02759, 0.012492, 0.000179, 0.047313, -0.068053, -0.007179, -0.010233, -5.4606e-34, 0.025166, 0.034118, -0.024733, 0.047228, 0.003747, -0.070258, 0.006291, 0.152883, -0.036288, 0.040884, 0.03752, -0.066384, -0.109309, -0.053935, 0.051464, 0.013807, 0.092907, 0.094678, -0.09784, 0.016447, -0.061126, -0.06691, -0.066924, -0.012761, -0.011371, 0.042607, 0.07928, -0.032157, 0.001585, -0.10567, -0.076343, -0.028213, 0.052881, 0.011874, 0.040783, -0.024232, 0.019623, -0.009142, -0.017977, 0.011064, 0.063624, 0.007392, -0.048477, 0.027756, -0.083771, -0.087622, -0.10184, -0.039955, -0.040235, 0.06248, -0.033642, -0.056431, 0.062244, -0.076749, -0.074775, 0.01314, 0.028267, 0.00843, -0.05746, 0.035314, -0.014488, 0.017557, -0.065159, 0.017372, 0.076326, -0.023451, -0.064457, 0.051611, -0.089624, 0.044742, 0.030695, 0.037951, -0.030822, -0.086775, -0.046732, -0.077627, 0.065684, 0.02859, -0.032896, -0.045175, -0.029318, -0.050483, -0.017911, 0.067584, 0.076418, 0.007941, -0.043728, 0.021922, 0.003156, 0.045067, 0.016599, 0.031952, 0.063431, 0.118829, 0.027209, -2.0099e-08, -0.030052, -0.020226, 0.02799, 0.033707, 0.045369, 0.044223, -0.004201, 0.02209, 0.005018, 0.044784, -0.00522, 0.009344, -0.086125, -0.030412, 0.001503, -0.075738, -0.027691, -0.012998, -0.096883, 0.009345, -0.098639, -0.012999, -0.014733, -0.023915, -0.001093, 0.031345, -0.010145, 0.144356, 0.050369, -0.013375, -0.024485, 0.033877, -0.05207, -0.002538, -0.037734, 0.02646, 0.039215, -0.059336, 0.036362, 0.020788, -0.054643, 0.002337, -0.001766, 0.045326, 0.100353, -0.065405, -0.001118, -0.030746, -0.058903, -0.058318, -0.022108, -0.031307, -0.073587, 0.097973, -0.027214, -0.044981, 0.05254, 0.042965, 0.002071, 0.074197, 0.003986, -0.068829, -0.012316, -0.061178], 91455473-212e-4c6e-8bec-1da06779ae10, 99c4d825-d262-4a95-a04e-cc72e7e273c1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LS534S','Go Away Annotation T-Shirt','LS534',{'ls534.png'},[-0.036608, 0.17069, -6.8e-05, 0.013803, 0.155039, 0.032511, 0.115352, -0.017075, 0.02312, -0.053608, 0.036347, 0.00325, 0.018615, 0.045028, 0.024062, 0.053021, 0.016931, 0.014446, -0.035558, -0.055052, -0.023493, 0.055527, 0.062397, 0.077756, -0.011309, -0.055835, -0.052177, -0.028795, -0.014989, -0.009282, -0.03812, 0.089499, -0.003033, 0.005257, -0.030212, -0.039765, 0.019952, 0.104384, 0.023942, 0.028156, 0.000343, -0.096789, -0.067907, 0.030879, 0.037792, 0.091158, -0.006283, 0.014304, -0.063456, 0.096292, 0.030419, -0.083409, -0.010061, -0.054874, 0.01327, 0.035466, 0.094629, -0.042095, -0.05736, 0.024775, 0.080676, -0.052107, -0.076339, 0.061823, 0.078378, -0.000108, 0.046956, 0.038856, -0.026526, 0.017161, 0.059612, -0.018435, 0.026958, 0.102243, 0.039013, 0.071186, 0.033843, -0.017657, 0.03598, -0.024247, -0.056728, 0.045184, 0.054087, -0.025071, -0.048991, 0.02662, -0.016531, -0.009302, 0.046526, -0.003961, -0.049663, 0.007909, 0.052308, 0.017191, -0.021396, -0.023883, -0.12157, -0.015862, -0.063323, 0.119512, 0.019032, 0.037145, 0.031725, 0.037319, 0.011787, -0.061692, -0.006838, 0.010912, 0.064413, -0.000109, -0.021548, 0.022913, -0.022013, -0.005394, -0.007294, -0.034773, 0.085055, 0.09958, 0.084548, -0.029302, -0.028561, 0.01266, -0.079814, 0.024768, 0.015278, -0.038034, -0.021359, -8.1697e-35, 0.073428, 0.055485, -0.044437, 0.075282, 0.02274, -0.023128, -0.040903, -0.116771, 0.016573, -0.003524, 0.030685, -0.045409, -0.019008, 0.042953, -0.006942, -0.000543, -0.00102, -0.014377, -0.048558, -0.054464, -0.004539, 0.111372, -0.002005, -0.060866, -0.022815, 0.10073, 0.008343, -0.069105, -0.03674, 0.031958, 0.045063, -0.013123, 0.047586, 0.007698, -0.003711, -0.048613, -0.033931, 0.002353, 0.030301, -0.001543, 0.064156, 0.046028, -0.001938, 0.010193, 0.013444, 0.002869, 0.081806, 0.012772, 0.014697, -0.014563, 0.065606, -0.010437, -0.012719, -0.130164, -0.088925, -0.078527, -0.007429, 0.009874, 0.02923, -0.073438, -0.004822, 0.034371, 0.065097, -0.003957, -0.019316, 0.000311, -0.058143, -0.042109, -0.020749, -0.089716, -0.041534, 0.049955, -0.001552, -0.021463, -0.021696, -0.079449, 0.065888, -0.034232, 0.012767, -0.033467, -0.043992, -0.04138, -0.012641, -0.015642, 0.005837, -0.055145, 0.071815, -0.067067, 0.02759, 0.012492, 0.000179, 0.047313, -0.068053, -0.007179, -0.010233, -5.4606e-34, 0.025166, 0.034118, -0.024733, 0.047228, 0.003747, -0.070258, 0.006291, 0.152883, -0.036288, 0.040884, 0.03752, -0.066384, -0.109309, -0.053935, 0.051464, 0.013807, 0.092907, 0.094678, -0.09784, 0.016447, -0.061126, -0.06691, -0.066924, -0.012761, -0.011371, 0.042607, 0.07928, -0.032157, 0.001585, -0.10567, -0.076343, -0.028213, 0.052881, 0.011874, 0.040783, -0.024232, 0.019623, -0.009142, -0.017977, 0.011064, 0.063624, 0.007392, -0.048477, 0.027756, -0.083771, -0.087622, -0.10184, -0.039955, -0.040235, 0.06248, -0.033642, -0.056431, 0.062244, -0.076749, -0.074775, 0.01314, 0.028267, 0.00843, -0.05746, 0.035314, -0.014488, 0.017557, -0.065159, 0.017372, 0.076326, -0.023451, -0.064457, 0.051611, -0.089624, 0.044742, 0.030695, 0.037951, -0.030822, -0.086775, -0.046732, -0.077627, 0.065684, 0.02859, -0.032896, -0.045175, -0.029318, -0.050483, -0.017911, 0.067584, 0.076418, 0.007941, -0.043728, 0.021922, 0.003156, 0.045067, 0.016599, 0.031952, 0.063431, 0.118829, 0.027209, -2.0099e-08, -0.030052, -0.020226, 0.02799, 0.033707, 0.045369, 0.044223, -0.004201, 0.02209, 0.005018, 0.044784, -0.00522, 0.009344, -0.086125, -0.030412, 0.001503, -0.075738, -0.027691, -0.012998, -0.096883, 0.009345, -0.098639, -0.012999, -0.014733, -0.023915, -0.001093, 0.031345, -0.010145, 0.144356, 0.050369, -0.013375, -0.024485, 0.033877, -0.05207, -0.002538, -0.037734, 0.02646, 0.039215, -0.059336, 0.036362, 0.020788, -0.054643, 0.002337, -0.001766, 0.045326, 0.100353, -0.065405, -0.001118, -0.030746, -0.058903, -0.058318, -0.022108, -0.031307, -0.073587, 0.097973, -0.027214, -0.044981, 0.05254, 0.042965, 0.002071, 0.074197, 0.003986, -0.068829, -0.012316, -0.061178], 91455473-212e-4c6e-8bec-1da06779ae10, 99c4d825-d262-4a95-a04e-cc72e7e273c1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LS534XL','Go Away Annotation T-Shirt','LS534',{'ls534.png'},[-0.036608, 0.17069, -6.8e-05, 0.013803, 0.155039, 0.032511, 0.115352, -0.017075, 0.02312, -0.053608, 0.036347, 0.00325, 0.018615, 0.045028, 0.024062, 0.053021, 0.016931, 0.014446, -0.035558, -0.055052, -0.023493, 0.055527, 0.062397, 0.077756, -0.011309, -0.055835, -0.052177, -0.028795, -0.014989, -0.009282, -0.03812, 0.089499, -0.003033, 0.005257, -0.030212, -0.039765, 0.019952, 0.104384, 0.023942, 0.028156, 0.000343, -0.096789, -0.067907, 0.030879, 0.037792, 0.091158, -0.006283, 0.014304, -0.063456, 0.096292, 0.030419, -0.083409, -0.010061, -0.054874, 0.01327, 0.035466, 0.094629, -0.042095, -0.05736, 0.024775, 0.080676, -0.052107, -0.076339, 0.061823, 0.078378, -0.000108, 0.046956, 0.038856, -0.026526, 0.017161, 0.059612, -0.018435, 0.026958, 0.102243, 0.039013, 0.071186, 0.033843, -0.017657, 0.03598, -0.024247, -0.056728, 0.045184, 0.054087, -0.025071, -0.048991, 0.02662, -0.016531, -0.009302, 0.046526, -0.003961, -0.049663, 0.007909, 0.052308, 0.017191, -0.021396, -0.023883, -0.12157, -0.015862, -0.063323, 0.119512, 0.019032, 0.037145, 0.031725, 0.037319, 0.011787, -0.061692, -0.006838, 0.010912, 0.064413, -0.000109, -0.021548, 0.022913, -0.022013, -0.005394, -0.007294, -0.034773, 0.085055, 0.09958, 0.084548, -0.029302, -0.028561, 0.01266, -0.079814, 0.024768, 0.015278, -0.038034, -0.021359, -8.1697e-35, 0.073428, 0.055485, -0.044437, 0.075282, 0.02274, -0.023128, -0.040903, -0.116771, 0.016573, -0.003524, 0.030685, -0.045409, -0.019008, 0.042953, -0.006942, -0.000543, -0.00102, -0.014377, -0.048558, -0.054464, -0.004539, 0.111372, -0.002005, -0.060866, -0.022815, 0.10073, 0.008343, -0.069105, -0.03674, 0.031958, 0.045063, -0.013123, 0.047586, 0.007698, -0.003711, -0.048613, -0.033931, 0.002353, 0.030301, -0.001543, 0.064156, 0.046028, -0.001938, 0.010193, 0.013444, 0.002869, 0.081806, 0.012772, 0.014697, -0.014563, 0.065606, -0.010437, -0.012719, -0.130164, -0.088925, -0.078527, -0.007429, 0.009874, 0.02923, -0.073438, -0.004822, 0.034371, 0.065097, -0.003957, -0.019316, 0.000311, -0.058143, -0.042109, -0.020749, -0.089716, -0.041534, 0.049955, -0.001552, -0.021463, -0.021696, -0.079449, 0.065888, -0.034232, 0.012767, -0.033467, -0.043992, -0.04138, -0.012641, -0.015642, 0.005837, -0.055145, 0.071815, -0.067067, 0.02759, 0.012492, 0.000179, 0.047313, -0.068053, -0.007179, -0.010233, -5.4606e-34, 0.025166, 0.034118, -0.024733, 0.047228, 0.003747, -0.070258, 0.006291, 0.152883, -0.036288, 0.040884, 0.03752, -0.066384, -0.109309, -0.053935, 0.051464, 0.013807, 0.092907, 0.094678, -0.09784, 0.016447, -0.061126, -0.06691, -0.066924, -0.012761, -0.011371, 0.042607, 0.07928, -0.032157, 0.001585, -0.10567, -0.076343, -0.028213, 0.052881, 0.011874, 0.040783, -0.024232, 0.019623, -0.009142, -0.017977, 0.011064, 0.063624, 0.007392, -0.048477, 0.027756, -0.083771, -0.087622, -0.10184, -0.039955, -0.040235, 0.06248, -0.033642, -0.056431, 0.062244, -0.076749, -0.074775, 0.01314, 0.028267, 0.00843, -0.05746, 0.035314, -0.014488, 0.017557, -0.065159, 0.017372, 0.076326, -0.023451, -0.064457, 0.051611, -0.089624, 0.044742, 0.030695, 0.037951, -0.030822, -0.086775, -0.046732, -0.077627, 0.065684, 0.02859, -0.032896, -0.045175, -0.029318, -0.050483, -0.017911, 0.067584, 0.076418, 0.007941, -0.043728, 0.021922, 0.003156, 0.045067, 0.016599, 0.031952, 0.063431, 0.118829, 0.027209, -2.0099e-08, -0.030052, -0.020226, 0.02799, 0.033707, 0.045369, 0.044223, -0.004201, 0.02209, 0.005018, 0.044784, -0.00522, 0.009344, -0.086125, -0.030412, 0.001503, -0.075738, -0.027691, -0.012998, -0.096883, 0.009345, -0.098639, -0.012999, -0.014733, -0.023915, -0.001093, 0.031345, -0.010145, 0.144356, 0.050369, -0.013375, -0.024485, 0.033877, -0.05207, -0.002538, -0.037734, 0.02646, 0.039215, -0.059336, 0.036362, 0.020788, -0.054643, 0.002337, -0.001766, 0.045326, 0.100353, -0.065405, -0.001118, -0.030746, -0.058903, -0.058318, -0.022108, -0.031307, -0.073587, 0.097973, -0.027214, -0.044981, 0.05254, 0.042965, 0.002071, 0.074197, 0.003986, -0.068829, -0.012316, -0.061178], 91455473-212e-4c6e-8bec-1da06779ae10, 99c4d825-d262-4a95-a04e-cc72e7e273c1);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LN3552XL','Your Face is an @Autowired @Bean T-Shirt','LN355',{'ln355.png'},[-0.09481, 0.045921, 0.01908, -0.018822, 0.094358, -0.033628, 0.085459, 0.015029, 0.009334, 0.023442, 0.032211, -0.039052, 0.032691, -0.016554, 0.034732, -0.036606, 0.048008, -0.066033, 0.053886, -0.049179, -0.017885, 0.041332, 0.043886, -0.014015, 0.012283, -0.01413, 0.023217, -0.020125, -0.126084, -0.033033, -0.092513, -0.004094, -0.055425, -0.007057, -0.08688, -0.009958, -0.021985, 0.084366, -0.001591, -0.062545, -0.065596, -0.144468, -0.008839, 0.005783, 0.035806, 0.027193, 0.02911, 0.032106, -0.053266, -0.107448, 0.010244, -0.055171, 0.076525, -0.02015, -0.048959, 0.08629, 0.030101, 0.004034, 0.049003, 0.130074, 0.009848, 0.002981, -0.018423, 0.040594, 0.016324, -0.069149, 0.007763, -0.000184, -0.057244, -0.043882, 0.056481, -0.036526, -0.060854, 0.049025, 0.070292, 0.056004, 0.059603, -0.014093, 0.053709, 0.109867, -0.125465, 0.021548, 0.024611, -0.027255, -0.047741, 0.00928, -0.02037, -0.044778, -0.069206, 0.022306, -0.010159, 0.051123, 0.00568, 0.00161, -0.042841, 0.012698, -0.051324, -0.031098, -0.116519, 0.121702, -0.014741, -0.028077, 0.02708, 0.083381, 0.077409, -0.049605, -0.031197, 0.045046, 0.077005, -0.090363, 0.020257, 0.000284, -0.02365, 0.001152, 0.034671, -0.072439, -0.014569, 0.053299, 0.097017, -0.040281, -0.033849, -0.039002, -0.052259, -0.079562, 0.013326, -0.062795, -0.000262, -2.5158e-33, 0.048052, 0.111954, 0.006392, 0.066856, -0.048434, 0.004697, -0.074962, -0.019848, -0.000541, 0.056096, -0.090624, 0.028151, -0.134424, 0.081959, -0.009588, 0.005233, -0.056705, -0.027663, -0.009634, -0.068687, -0.019181, 0.021579, 0.029736, -0.003752, -0.064612, 0.072206, 0.067816, -0.099939, -0.010352, 0.050665, -0.005061, 0.066762, 0.073979, -0.015582, 0.013203, -0.079359, 0.001711, 0.013001, -0.011402, 0.045852, 0.052238, -0.038529, -0.030899, -0.0631, 0.008019, 0.036128, 0.14057, 0.058045, -0.010327, 0.050603, 0.0242, -0.045193, 0.017187, -0.06545, -0.042649, 0.048363, -0.011385, 0.056152, 0.021064, 0.026284, -0.007472, 0.038379, 0.043741, -0.004898, -0.042536, -0.043065, -0.010307, -0.04352, -0.010936, -0.06694, 0.020214, 0.037688, 0.046133, 0.015693, 0.026647, -0.083017, 0.071479, 0.121818, -0.029598, 0.045362, -0.018292, 0.026782, -0.020145, -0.024812, 0.083834, 0.053491, 0.003986, -0.072848, 0.00742, 0.037701, 0.058511, 0.038883, -0.012578, 0.031437, -0.010416, 1.3475e-33, 0.03954, -0.012607, 0.025139, 0.01034, 0.016385, -0.036682, 0.04829, 0.129614, -0.06864, 0.071075, 0.052056, 0.014204, -0.09284, -0.09421, 0.089497, -0.051746, 0.072725, -0.036049, -0.112402, 0.040431, -0.110631, 0.006999, 0.017367, -0.011863, 0.019505, -0.009257, 0.102337, 0.077223, -0.031278, -0.00106, 0.030653, 0.039768, -0.019613, 0.080902, -0.025992, -0.033587, -0.036791, -0.008079, 0.01133, -0.035639, 0.009845, -0.028254, -0.027843, 0.090118, -0.039578, -0.073996, -0.103852, -0.124097, -0.033517, 0.112282, -0.0202, -0.031629, 0.030903, -0.036458, -0.045907, 0.02024, 0.030892, 0.084995, 0.050889, 0.061072, 0.008703, -0.038506, 0.010281, -0.00198, 0.041941, -0.022902, -0.016502, 0.004139, 0.034879, 0.048904, 0.055029, -0.014042, -0.05395, -0.067702, 0.027891, -0.052883, 0.004606, 0.05935, -0.03666, 0.000506, -0.062663, -0.009489, -0.02286, 0.020377, 0.015392, -0.031319, 0.024558, 0.048058, -0.005835, 0.042359, 0.017187, 0.103138, -0.037442, 0.073332, -0.002722, -1.9469e-08, 0.010256, -0.013013, 0.019793, -0.009293, 0.044898, 0.06434, 0.003189, -0.049714, -0.036161, -0.017065, -0.009412, 0.024307, -0.01664, 0.032413, -0.007326, -0.045998, -0.065201, 0.02649, -0.057185, -0.016784, -0.087701, 0.013753, 0.037569, -0.003874, 0.064465, -0.010263, 0.040526, 0.134563, 0.026193, 0.019204, -0.028249, 0.009399, -0.056405, -0.007491, -0.078234, 0.032858, -0.014138, -0.048396, -0.011551, -0.095411, -0.01722, -0.008977, -0.002353, 0.03403, -0.087162, 0.025286, 0.029897, 0.024798, -0.011015, 0.010609, -0.052643, -0.018772, -0.039519, 0.084192, -0.012706, -0.014301, 0.083297, -0.031679, 0.002024, 0.012814, 0.001586, -0.020503, -0.067949, -0.047052], 91455473-212e-4c6e-8bec-1da06779ae10, 3fa13eee-d057-48d0-b0ae-2d83af9e3e3e);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LN355L','Your Face is an @Autowired @Bean T-Shirt','LN355',{'ln355.png'},[-0.09481, 0.045921, 0.01908, -0.018822, 0.094358, -0.033628, 0.085459, 0.015029, 0.009334, 0.023442, 0.032211, -0.039052, 0.032691, -0.016554, 0.034732, -0.036606, 0.048008, -0.066033, 0.053886, -0.049179, -0.017885, 0.041332, 0.043886, -0.014015, 0.012283, -0.01413, 0.023217, -0.020125, -0.126084, -0.033033, -0.092513, -0.004094, -0.055425, -0.007057, -0.08688, -0.009958, -0.021985, 0.084366, -0.001591, -0.062545, -0.065596, -0.144468, -0.008839, 0.005783, 0.035806, 0.027193, 0.02911, 0.032106, -0.053266, -0.107448, 0.010244, -0.055171, 0.076525, -0.02015, -0.048959, 0.08629, 0.030101, 0.004034, 0.049003, 0.130074, 0.009848, 0.002981, -0.018423, 0.040594, 0.016324, -0.069149, 0.007763, -0.000184, -0.057244, -0.043882, 0.056481, -0.036526, -0.060854, 0.049025, 0.070292, 0.056004, 0.059603, -0.014093, 0.053709, 0.109867, -0.125465, 0.021548, 0.024611, -0.027255, -0.047741, 0.00928, -0.02037, -0.044778, -0.069206, 0.022306, -0.010159, 0.051123, 0.00568, 0.00161, -0.042841, 0.012698, -0.051324, -0.031098, -0.116519, 0.121702, -0.014741, -0.028077, 0.02708, 0.083381, 0.077409, -0.049605, -0.031197, 0.045046, 0.077005, -0.090363, 0.020257, 0.000284, -0.02365, 0.001152, 0.034671, -0.072439, -0.014569, 0.053299, 0.097017, -0.040281, -0.033849, -0.039002, -0.052259, -0.079562, 0.013326, -0.062795, -0.000262, -2.5158e-33, 0.048052, 0.111954, 0.006392, 0.066856, -0.048434, 0.004697, -0.074962, -0.019848, -0.000541, 0.056096, -0.090624, 0.028151, -0.134424, 0.081959, -0.009588, 0.005233, -0.056705, -0.027663, -0.009634, -0.068687, -0.019181, 0.021579, 0.029736, -0.003752, -0.064612, 0.072206, 0.067816, -0.099939, -0.010352, 0.050665, -0.005061, 0.066762, 0.073979, -0.015582, 0.013203, -0.079359, 0.001711, 0.013001, -0.011402, 0.045852, 0.052238, -0.038529, -0.030899, -0.0631, 0.008019, 0.036128, 0.14057, 0.058045, -0.010327, 0.050603, 0.0242, -0.045193, 0.017187, -0.06545, -0.042649, 0.048363, -0.011385, 0.056152, 0.021064, 0.026284, -0.007472, 0.038379, 0.043741, -0.004898, -0.042536, -0.043065, -0.010307, -0.04352, -0.010936, -0.06694, 0.020214, 0.037688, 0.046133, 0.015693, 0.026647, -0.083017, 0.071479, 0.121818, -0.029598, 0.045362, -0.018292, 0.026782, -0.020145, -0.024812, 0.083834, 0.053491, 0.003986, -0.072848, 0.00742, 0.037701, 0.058511, 0.038883, -0.012578, 0.031437, -0.010416, 1.3475e-33, 0.03954, -0.012607, 0.025139, 0.01034, 0.016385, -0.036682, 0.04829, 0.129614, -0.06864, 0.071075, 0.052056, 0.014204, -0.09284, -0.09421, 0.089497, -0.051746, 0.072725, -0.036049, -0.112402, 0.040431, -0.110631, 0.006999, 0.017367, -0.011863, 0.019505, -0.009257, 0.102337, 0.077223, -0.031278, -0.00106, 0.030653, 0.039768, -0.019613, 0.080902, -0.025992, -0.033587, -0.036791, -0.008079, 0.01133, -0.035639, 0.009845, -0.028254, -0.027843, 0.090118, -0.039578, -0.073996, -0.103852, -0.124097, -0.033517, 0.112282, -0.0202, -0.031629, 0.030903, -0.036458, -0.045907, 0.02024, 0.030892, 0.084995, 0.050889, 0.061072, 0.008703, -0.038506, 0.010281, -0.00198, 0.041941, -0.022902, -0.016502, 0.004139, 0.034879, 0.048904, 0.055029, -0.014042, -0.05395, -0.067702, 0.027891, -0.052883, 0.004606, 0.05935, -0.03666, 0.000506, -0.062663, -0.009489, -0.02286, 0.020377, 0.015392, -0.031319, 0.024558, 0.048058, -0.005835, 0.042359, 0.017187, 0.103138, -0.037442, 0.073332, -0.002722, -1.9469e-08, 0.010256, -0.013013, 0.019793, -0.009293, 0.044898, 0.06434, 0.003189, -0.049714, -0.036161, -0.017065, -0.009412, 0.024307, -0.01664, 0.032413, -0.007326, -0.045998, -0.065201, 0.02649, -0.057185, -0.016784, -0.087701, 0.013753, 0.037569, -0.003874, 0.064465, -0.010263, 0.040526, 0.134563, 0.026193, 0.019204, -0.028249, 0.009399, -0.056405, -0.007491, -0.078234, 0.032858, -0.014138, -0.048396, -0.011551, -0.095411, -0.01722, -0.008977, -0.002353, 0.03403, -0.087162, 0.025286, 0.029897, 0.024798, -0.011015, 0.010609, -0.052643, -0.018772, -0.039519, 0.084192, -0.012706, -0.014301, 0.083297, -0.031679, 0.002024, 0.012814, 0.001586, -0.020503, -0.067949, -0.047052], 91455473-212e-4c6e-8bec-1da06779ae10, 3fa13eee-d057-48d0-b0ae-2d83af9e3e3e);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LN355M','Your Face is an @Autowired @Bean T-Shirt','LN355',{'ln355.png'},[-0.09481, 0.045921, 0.01908, -0.018822, 0.094358, -0.033628, 0.085459, 0.015029, 0.009334, 0.023442, 0.032211, -0.039052, 0.032691, -0.016554, 0.034732, -0.036606, 0.048008, -0.066033, 0.053886, -0.049179, -0.017885, 0.041332, 0.043886, -0.014015, 0.012283, -0.01413, 0.023217, -0.020125, -0.126084, -0.033033, -0.092513, -0.004094, -0.055425, -0.007057, -0.08688, -0.009958, -0.021985, 0.084366, -0.001591, -0.062545, -0.065596, -0.144468, -0.008839, 0.005783, 0.035806, 0.027193, 0.02911, 0.032106, -0.053266, -0.107448, 0.010244, -0.055171, 0.076525, -0.02015, -0.048959, 0.08629, 0.030101, 0.004034, 0.049003, 0.130074, 0.009848, 0.002981, -0.018423, 0.040594, 0.016324, -0.069149, 0.007763, -0.000184, -0.057244, -0.043882, 0.056481, -0.036526, -0.060854, 0.049025, 0.070292, 0.056004, 0.059603, -0.014093, 0.053709, 0.109867, -0.125465, 0.021548, 0.024611, -0.027255, -0.047741, 0.00928, -0.02037, -0.044778, -0.069206, 0.022306, -0.010159, 0.051123, 0.00568, 0.00161, -0.042841, 0.012698, -0.051324, -0.031098, -0.116519, 0.121702, -0.014741, -0.028077, 0.02708, 0.083381, 0.077409, -0.049605, -0.031197, 0.045046, 0.077005, -0.090363, 0.020257, 0.000284, -0.02365, 0.001152, 0.034671, -0.072439, -0.014569, 0.053299, 0.097017, -0.040281, -0.033849, -0.039002, -0.052259, -0.079562, 0.013326, -0.062795, -0.000262, -2.5158e-33, 0.048052, 0.111954, 0.006392, 0.066856, -0.048434, 0.004697, -0.074962, -0.019848, -0.000541, 0.056096, -0.090624, 0.028151, -0.134424, 0.081959, -0.009588, 0.005233, -0.056705, -0.027663, -0.009634, -0.068687, -0.019181, 0.021579, 0.029736, -0.003752, -0.064612, 0.072206, 0.067816, -0.099939, -0.010352, 0.050665, -0.005061, 0.066762, 0.073979, -0.015582, 0.013203, -0.079359, 0.001711, 0.013001, -0.011402, 0.045852, 0.052238, -0.038529, -0.030899, -0.0631, 0.008019, 0.036128, 0.14057, 0.058045, -0.010327, 0.050603, 0.0242, -0.045193, 0.017187, -0.06545, -0.042649, 0.048363, -0.011385, 0.056152, 0.021064, 0.026284, -0.007472, 0.038379, 0.043741, -0.004898, -0.042536, -0.043065, -0.010307, -0.04352, -0.010936, -0.06694, 0.020214, 0.037688, 0.046133, 0.015693, 0.026647, -0.083017, 0.071479, 0.121818, -0.029598, 0.045362, -0.018292, 0.026782, -0.020145, -0.024812, 0.083834, 0.053491, 0.003986, -0.072848, 0.00742, 0.037701, 0.058511, 0.038883, -0.012578, 0.031437, -0.010416, 1.3475e-33, 0.03954, -0.012607, 0.025139, 0.01034, 0.016385, -0.036682, 0.04829, 0.129614, -0.06864, 0.071075, 0.052056, 0.014204, -0.09284, -0.09421, 0.089497, -0.051746, 0.072725, -0.036049, -0.112402, 0.040431, -0.110631, 0.006999, 0.017367, -0.011863, 0.019505, -0.009257, 0.102337, 0.077223, -0.031278, -0.00106, 0.030653, 0.039768, -0.019613, 0.080902, -0.025992, -0.033587, -0.036791, -0.008079, 0.01133, -0.035639, 0.009845, -0.028254, -0.027843, 0.090118, -0.039578, -0.073996, -0.103852, -0.124097, -0.033517, 0.112282, -0.0202, -0.031629, 0.030903, -0.036458, -0.045907, 0.02024, 0.030892, 0.084995, 0.050889, 0.061072, 0.008703, -0.038506, 0.010281, -0.00198, 0.041941, -0.022902, -0.016502, 0.004139, 0.034879, 0.048904, 0.055029, -0.014042, -0.05395, -0.067702, 0.027891, -0.052883, 0.004606, 0.05935, -0.03666, 0.000506, -0.062663, -0.009489, -0.02286, 0.020377, 0.015392, -0.031319, 0.024558, 0.048058, -0.005835, 0.042359, 0.017187, 0.103138, -0.037442, 0.073332, -0.002722, -1.9469e-08, 0.010256, -0.013013, 0.019793, -0.009293, 0.044898, 0.06434, 0.003189, -0.049714, -0.036161, -0.017065, -0.009412, 0.024307, -0.01664, 0.032413, -0.007326, -0.045998, -0.065201, 0.02649, -0.057185, -0.016784, -0.087701, 0.013753, 0.037569, -0.003874, 0.064465, -0.010263, 0.040526, 0.134563, 0.026193, 0.019204, -0.028249, 0.009399, -0.056405, -0.007491, -0.078234, 0.032858, -0.014138, -0.048396, -0.011551, -0.095411, -0.01722, -0.008977, -0.002353, 0.03403, -0.087162, 0.025286, 0.029897, 0.024798, -0.011015, 0.010609, -0.052643, -0.018772, -0.039519, 0.084192, -0.012706, -0.014301, 0.083297, -0.031679, 0.002024, 0.012814, 0.001586, -0.020503, -0.067949, -0.047052], 91455473-212e-4c6e-8bec-1da06779ae10, 3fa13eee-d057-48d0-b0ae-2d83af9e3e3e);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LN355S','Your Face is an @Autowired @Bean T-Shirt','LN355',{'ln355.png'},[-0.09481, 0.045921, 0.01908, -0.018822, 0.094358, -0.033628, 0.085459, 0.015029, 0.009334, 0.023442, 0.032211, -0.039052, 0.032691, -0.016554, 0.034732, -0.036606, 0.048008, -0.066033, 0.053886, -0.049179, -0.017885, 0.041332, 0.043886, -0.014015, 0.012283, -0.01413, 0.023217, -0.020125, -0.126084, -0.033033, -0.092513, -0.004094, -0.055425, -0.007057, -0.08688, -0.009958, -0.021985, 0.084366, -0.001591, -0.062545, -0.065596, -0.144468, -0.008839, 0.005783, 0.035806, 0.027193, 0.02911, 0.032106, -0.053266, -0.107448, 0.010244, -0.055171, 0.076525, -0.02015, -0.048959, 0.08629, 0.030101, 0.004034, 0.049003, 0.130074, 0.009848, 0.002981, -0.018423, 0.040594, 0.016324, -0.069149, 0.007763, -0.000184, -0.057244, -0.043882, 0.056481, -0.036526, -0.060854, 0.049025, 0.070292, 0.056004, 0.059603, -0.014093, 0.053709, 0.109867, -0.125465, 0.021548, 0.024611, -0.027255, -0.047741, 0.00928, -0.02037, -0.044778, -0.069206, 0.022306, -0.010159, 0.051123, 0.00568, 0.00161, -0.042841, 0.012698, -0.051324, -0.031098, -0.116519, 0.121702, -0.014741, -0.028077, 0.02708, 0.083381, 0.077409, -0.049605, -0.031197, 0.045046, 0.077005, -0.090363, 0.020257, 0.000284, -0.02365, 0.001152, 0.034671, -0.072439, -0.014569, 0.053299, 0.097017, -0.040281, -0.033849, -0.039002, -0.052259, -0.079562, 0.013326, -0.062795, -0.000262, -2.5158e-33, 0.048052, 0.111954, 0.006392, 0.066856, -0.048434, 0.004697, -0.074962, -0.019848, -0.000541, 0.056096, -0.090624, 0.028151, -0.134424, 0.081959, -0.009588, 0.005233, -0.056705, -0.027663, -0.009634, -0.068687, -0.019181, 0.021579, 0.029736, -0.003752, -0.064612, 0.072206, 0.067816, -0.099939, -0.010352, 0.050665, -0.005061, 0.066762, 0.073979, -0.015582, 0.013203, -0.079359, 0.001711, 0.013001, -0.011402, 0.045852, 0.052238, -0.038529, -0.030899, -0.0631, 0.008019, 0.036128, 0.14057, 0.058045, -0.010327, 0.050603, 0.0242, -0.045193, 0.017187, -0.06545, -0.042649, 0.048363, -0.011385, 0.056152, 0.021064, 0.026284, -0.007472, 0.038379, 0.043741, -0.004898, -0.042536, -0.043065, -0.010307, -0.04352, -0.010936, -0.06694, 0.020214, 0.037688, 0.046133, 0.015693, 0.026647, -0.083017, 0.071479, 0.121818, -0.029598, 0.045362, -0.018292, 0.026782, -0.020145, -0.024812, 0.083834, 0.053491, 0.003986, -0.072848, 0.00742, 0.037701, 0.058511, 0.038883, -0.012578, 0.031437, -0.010416, 1.3475e-33, 0.03954, -0.012607, 0.025139, 0.01034, 0.016385, -0.036682, 0.04829, 0.129614, -0.06864, 0.071075, 0.052056, 0.014204, -0.09284, -0.09421, 0.089497, -0.051746, 0.072725, -0.036049, -0.112402, 0.040431, -0.110631, 0.006999, 0.017367, -0.011863, 0.019505, -0.009257, 0.102337, 0.077223, -0.031278, -0.00106, 0.030653, 0.039768, -0.019613, 0.080902, -0.025992, -0.033587, -0.036791, -0.008079, 0.01133, -0.035639, 0.009845, -0.028254, -0.027843, 0.090118, -0.039578, -0.073996, -0.103852, -0.124097, -0.033517, 0.112282, -0.0202, -0.031629, 0.030903, -0.036458, -0.045907, 0.02024, 0.030892, 0.084995, 0.050889, 0.061072, 0.008703, -0.038506, 0.010281, -0.00198, 0.041941, -0.022902, -0.016502, 0.004139, 0.034879, 0.048904, 0.055029, -0.014042, -0.05395, -0.067702, 0.027891, -0.052883, 0.004606, 0.05935, -0.03666, 0.000506, -0.062663, -0.009489, -0.02286, 0.020377, 0.015392, -0.031319, 0.024558, 0.048058, -0.005835, 0.042359, 0.017187, 0.103138, -0.037442, 0.073332, -0.002722, -1.9469e-08, 0.010256, -0.013013, 0.019793, -0.009293, 0.044898, 0.06434, 0.003189, -0.049714, -0.036161, -0.017065, -0.009412, 0.024307, -0.01664, 0.032413, -0.007326, -0.045998, -0.065201, 0.02649, -0.057185, -0.016784, -0.087701, 0.013753, 0.037569, -0.003874, 0.064465, -0.010263, 0.040526, 0.134563, 0.026193, 0.019204, -0.028249, 0.009399, -0.056405, -0.007491, -0.078234, 0.032858, -0.014138, -0.048396, -0.011551, -0.095411, -0.01722, -0.008977, -0.002353, 0.03403, -0.087162, 0.025286, 0.029897, 0.024798, -0.011015, 0.010609, -0.052643, -0.018772, -0.039519, 0.084192, -0.012706, -0.014301, 0.083297, -0.031679, 0.002024, 0.012814, 0.001586, -0.020503, -0.067949, -0.047052], 91455473-212e-4c6e-8bec-1da06779ae10, 3fa13eee-d057-48d0-b0ae-2d83af9e3e3e);
INSERT INTO product_vectors (product_id, name, product_group, images, product_vector, parent_id, category_id)
VALUES('LN355XL','Your Face is an @Autowired @Bean T-Shirt','LN355',{'ln355.png'},[-0.09481, 0.045921, 0.01908, -0.018822, 0.094358, -0.033628, 0.085459, 0.015029, 0.009334, 0.023442, 0.032211, -0.039052, 0.032691, -0.016554, 0.034732, -0.036606, 0.048008, -0.066033, 0.053886, -0.049179, -0.017885, 0.041332, 0.043886, -0.014015, 0.012283, -0.01413, 0.023217, -0.020125, -0.126084, -0.033033, -0.092513, -0.004094, -0.055425, -0.007057, -0.08688, -0.009958, -0.021985, 0.084366, -0.001591, -0.062545, -0.065596, -0.144468, -0.008839, 0.005783, 0.035806, 0.027193, 0.02911, 0.032106, -0.053266, -0.107448, 0.010244, -0.055171, 0.076525, -0.02015, -0.048959, 0.08629, 0.030101, 0.004034, 0.049003, 0.130074, 0.009848, 0.002981, -0.018423, 0.040594, 0.016324, -0.069149, 0.007763, -0.000184, -0.057244, -0.043882, 0.056481, -0.036526, -0.060854, 0.049025, 0.070292, 0.056004, 0.059603, -0.014093, 0.053709, 0.109867, -0.125465, 0.021548, 0.024611, -0.027255, -0.047741, 0.00928, -0.02037, -0.044778, -0.069206, 0.022306, -0.010159, 0.051123, 0.00568, 0.00161, -0.042841, 0.012698, -0.051324, -0.031098, -0.116519, 0.121702, -0.014741, -0.028077, 0.02708, 0.083381, 0.077409, -0.049605, -0.031197, 0.045046, 0.077005, -0.090363, 0.020257, 0.000284, -0.02365, 0.001152, 0.034671, -0.072439, -0.014569, 0.053299, 0.097017, -0.040281, -0.033849, -0.039002, -0.052259, -0.079562, 0.013326, -0.062795, -0.000262, -2.5158e-33, 0.048052, 0.111954, 0.006392, 0.066856, -0.048434, 0.004697, -0.074962, -0.019848, -0.000541, 0.056096, -0.090624, 0.028151, -0.134424, 0.081959, -0.009588, 0.005233, -0.056705, -0.027663, -0.009634, -0.068687, -0.019181, 0.021579, 0.029736, -0.003752, -0.064612, 0.072206, 0.067816, -0.099939, -0.010352, 0.050665, -0.005061, 0.066762, 0.073979, -0.015582, 0.013203, -0.079359, 0.001711, 0.013001, -0.011402, 0.045852, 0.052238, -0.038529, -0.030899, -0.0631, 0.008019, 0.036128, 0.14057, 0.058045, -0.010327, 0.050603, 0.0242, -0.045193, 0.017187, -0.06545, -0.042649, 0.048363, -0.011385, 0.056152, 0.021064, 0.026284, -0.007472, 0.038379, 0.043741, -0.004898, -0.042536, -0.043065, -0.010307, -0.04352, -0.010936, -0.06694, 0.020214, 0.037688, 0.046133, 0.015693, 0.026647, -0.083017, 0.071479, 0.121818, -0.029598, 0.045362, -0.018292, 0.026782, -0.020145, -0.024812, 0.083834, 0.053491, 0.003986, -0.072848, 0.00742, 0.037701, 0.058511, 0.038883, -0.012578, 0.031437, -0.010416, 1.3475e-33, 0.03954, -0.012607, 0.025139, 0.01034, 0.016385, -0.036682, 0.04829, 0.129614, -0.06864, 0.071075, 0.052056, 0.014204, -0.09284, -0.09421, 0.089497, -0.051746, 0.072725, -0.036049, -0.112402, 0.040431, -0.110631, 0.006999, 0.017367, -0.011863, 0.019505, -0.009257, 0.102337, 0.077223, -0.031278, -0.00106, 0.030653, 0.039768, -0.019613, 0.080902, -0.025992, -0.033587, -0.036791, -0.008079, 0.01133, -0.035639, 0.009845, -0.028254, -0.027843, 0.090118, -0.039578, -0.073996, -0.103852, -0.124097, -0.033517, 0.112282, -0.0202, -0.031629, 0.030903, -0.036458, -0.045907, 0.02024, 0.030892, 0.084995, 0.050889, 0.061072, 0.008703, -0.038506, 0.010281, -0.00198, 0.041941, -0.022902, -0.016502, 0.004139, 0.034879, 0.048904, 0.055029, -0.014042, -0.05395, -0.067702, 0.027891, -0.052883, 0.004606, 0.05935, -0.03666, 0.000506, -0.062663, -0.009489, -0.02286, 0.020377, 0.015392, -0.031319, 0.024558, 0.048058, -0.005835, 0.042359, 0.017187, 0.103138, -0.037442, 0.073332, -0.002722, -1.9469e-08, 0.010256, -0.013013, 0.019793, -0.009293, 0.044898, 0.06434, 0.003189, -0.049714, -0.036161, -0.017065, -0.009412, 0.024307, -0.01664, 0.032413, -0.007326, -0.045998, -0.065201, 0.02649, -0.057185, -0.016784, -0.087701, 0.013753, 0.037569, -0.003874, 0.064465, -0.010263, 0.040526, 0.134563, 0.026193, 0.019204, -0.028249, 0.009399, -0.056405, -0.007491, -0.078234, 0.032858, -0.014138, -0.048396, -0.011551, -0.095411, -0.01722, -0.008977, -0.002353, 0.03403, -0.087162, 0.025286, 0.029897, 0.024798, -0.011015, 0.010609, -0.052643, -0.018772, -0.039519, 0.084192, -0.012706, -0.014301, 0.083297, -0.031679, 0.002024, 0.012814, 0.001586, -0.020503, -0.067949, -0.047052], 91455473-212e-4c6e-8bec-1da06779ae10, 3fa13eee-d057-48d0-b0ae-2d83af9e3e3e);
```

[🏠 Back to Table of Contents](#-table-of-contents)

## 5. Create your tokens

#### ✅ 5a. Create the Astra DB token

Following the [Manage Application Tokens docs](https://docs.datastax.com/en/astra/docs/manage-application-tokens.html) create a token with `Database Admnistrator` roles.

- Go the `Organization Settings`

- Go to `Token Management`

- Pick the role `Database Administrator` on the select box

- Click Generate token

**👁️ Walkthrough**

![image](data/img/astra-create-token.gif?raw=true)

This is what the token page looks like. You can now download the values as a CSV. We will need those values but you can also keep this window open for use later.

![image](data/img/astra-token.png?raw=true)

- `appToken:` We will use it as a api token Key to interact with APIs.

#### ✅ 5b. Save your DB token locally

To know more about roles of each token you can have a look to [this video.](https://www.youtube.com/watch?v=TUTCLsBuUd4&list=PL2g2h-wyI4SpWK1G3UaxXhzZc6aUFXbvL&index=8)

**Note: Make sure you don't close the window accidentally or otherwise - if you close this window before you copy the values, the application token is lost forever. They won't be available later for security reasons.**

> **⚠️ Important**
> ```
> The instructor will show you on screen how to create a token
> but will have to destroy to token immediately for security reasons.
> ```

We are now set with the database and credentials and will incorporate them into the application as we will see below.

[🏠 Back to Table of Contents](#-table-of-contents)

#### ✅ 5c. View the Astra Streaming token and connection details

Click on the "Connect" tab.  Take note of your tenant name and broker service URL.  It's a good idea to copy/paste those into a text editor for now.  When you're ready, click on the "Token Manager" link.

![image](data/img/broker_service_url.png?raw=true)

You should have one token created by default.  Click on the copy icon on the right.  Paste your token into a text editor for now.

![image](data/img/copy_stream_token.png?raw=true)

Later on, we will use this information to populate environment variables, allowing us to connect to our Astra Streaming tenant.  It will be similar to the example below:

```
export ASTRA_STREAM_TENANT=ecommerce-aaron
export ASTRA_STREAM_URL="pulsar+ssl://pulsar-gcp-uscentral1.streaming.datastax.com:6651"
export ASTRA_STREAM_TOKEN="eyJhMBhGYBlahBlahBlahNotARealToken37hOAv9t1fHIhJLAHw"
```

#### ✅ 5d. Save your Streaming token locally

## 6. Setup your application

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/aar0np/workshop_ecommerce_prototype)

### Know your Gitpod

Take a moment to read this entire section since it'll help you with the rest of the workshop as you'll be spending most of your time in Gitpod. If you're familiar with Gitpod, you can easily skip this entire section.

The extreme left side has the explorer view(1). The top left, middle to right is where you'll be editing files(2), etc. and the bottom left, middle to right is what we will refer to as the Gitpod terminal window(3) as shown below.

**👁️ Expected output**

![gitpod](data/img/gitpod-01-home-annotated.png?raw=true)


You can always get back to the file explorer view whenever by clicking on the hamburger menu on the top left followed by `View` and `Explorer` as shown below.

![gitpod](data/img/Filexplorer0.png?raw=true)


You can allow cutting and pasting into the window by clicking on `Allow` as shown below.

![gitpod](data/img/allow.png?raw=true)


✅ **6a: Enter the token**

To run the application you need to provide the credentials and identifier to the application. you will have to provide 4 values in total as shown below


Copy the environment sample file as below.

```
cp .env.example .env
```

Open the `.env` file as below.

```
gp open .env
```

- In Astra DB go back to home page by clicking the logo

- Select you database `demos` in the left panel and then copy values for `cloud-region` and `database-id` (clusterID) from the details page as shown below.

- *The DatabaseID is located on the home page*

![Ecom Welcome Screen](data/img/astra-config-1.png?raw=true)

- *The Database region (and keyspace) are located in the details page*

![Ecom Welcome Screen](data/img/astra-config-2.png?raw=true)

- Replace `application-token` with values shown on the Astra token screen or picking the values from the CSV token file your dowloaded before including the AstraCS: part of the token.


- *Make sure the Token looks something like (with AstraCS: preceeding `AstraCS:xxxxxxxxxxx:yyyyyyyyyyy`*

```yaml
# Copy this file to .env and fill in the appropriate values. Refer to README.md
# for instructions on where to find them.
export ASTRA_DB_ID=
export ASTRA_DB_REGION=
export ASTRA_DB_APP_TOKEN=
export ASTRA_DB_KEYSPACE=ecommerce
export ASTRA_STREAM_TENANT=
export ASTRA_STREAM_URL=
export ASTRA_STREAM_TOKEN=
export GOOGLE_CLIENT_ID=
export GOOGLE_CLIENT_SECRET=
```

Make sure to inject the environment variables by running the following command

```
source .env
```

Verify that the environment variables are properly setup with the following command

```
env | grep -i astra
```

You should see four environment variables (not shown here).


[🏠 Back to Table of Contents](#-table-of-contents)

## 7. Enable Social Login

Now that we're done with tests, let's `cd` to the top directory.

```
/workspace/workshop-ecommerce-app/
```

On a tab in a browser navigate to [https://console.cloud.google.com/apis/credentials](https://console.cloud.google.com/apis/credentials).

Consent to using APIs and services and you should finally be presented a screen that looks like below and pick values as shown.

![ouath](data/img/Oauthconsent1.png?raw=true)

Pick the appropriate values as shown below and complete the consent.

![ouath](data/img/Oauthconsent2.png?raw=true)

Make sure the project is setup for internal testing (and not for production) as shown below with the "MAKE INTERNAL".

![ouath](data/img/Oauthconsent3.png?raw=true)

Now click on the `credentials` tab, `+ CREATE CREDENTIALS` tab and finally the `OAuth Client ID` dropdown as shown in the following screen.

![ouath](data/img/Oauthcred0.png?raw=true)


You will be presented with a screen for entering the `Authorized JavaScript Origins` and `Authorized redirect URIs` as shown below.

You'll need the following URIs. Make a note of this. We will use `http` instead of `https` as illustrated below.

For the `Authorized JavaScript Origins` use the following value from the Gitpod terminal window,


```bash
echo $(gp url 8080 | sed 's/https/http/')
```

For the `Authorized redirect URIs` use the following from the GitPod terminal window.

```bash
echo $(gp url 8080 | sed 's/https/http/')/login/oauth2/code/google
```

Enter the respective values as shown below which enables URI redirection and SSO for the app.

![ouath](data/img/Oauthcred1.png?raw=true)


Make sure you enter the above values correctly as shown and hit `CREATE` on bottom as shown.

![ouath](data/img/Oauthcred2.png?raw=true)

Now you're ready to fetch the credentials  by using the copy 'n paste icons on right as shown below.

![ouath](data/img/Oauthcred3.png?raw=true)

You can copy and paste them in the `.env` file as entries for Google `GOOGLE_CLIENT_ID` and `GOOGLE_CLIENT_SECRET`.

[🏠 Back to Table of Contents](#-table-of-contents)

## 8. Run Unit Tests

The application is now set you should be able to interact with your DB. Let's demonstrate some capabilities.

✅ **8a: Use CqlSession**

Interaction with Cassandra are implemented in Java through drivers and the main Class is `CqlSession`.

Higher level frameworks like Spring, Spring Data, or even quarkus will rely on this object so let's make sure it is part of your Spring context with a `@SpringBootTest`.

Let's change to the sub-directory from the terminal window as shown below.

```
cd backend
```

Let's run the first test with the following command.


```bash
mvn test -Dtest=com.datastax.tutorials.Test01_Connectivity
```

**👁️ Expected output**

```bash
[..init...]
Execute some Cql (CqlSession)
+ Your Keyspace: sag_ecommerce
+ Product Categories:
Clothing
Cups and Mugs
Tech Accessories
Wall Decor
List Databases available in your Organization (AstraClient)
+ Your OrganizationID: e195fbea-79b6-4d60-9291-063d8c9e6364
+ Your Databases:
workshops	 : id=8c98b922-aeb0-4435-a0d5-a2788e23dff8, region=eu-central-1
sample_apps	 : id=c2d6bd3d-6112-47f6-9b66-b033e6174f0e, region=us-east-1
sdk_tests	 : id=a52f5879-3476-42d2-b5c9-81b18fc6d103, region=us-east-1
metrics	 : id=d7ded041-3cfb-4dd4-9957-e20003c3ebe2, region=us-east-1
```

✅ **8b: Working With Spring Data**

Spring Data allows Mapping `Object <=> Table` based on annotation at the java bean level. Then by convention CQL query will be executed under the hood.

```bash
mvn test -Dtest=com.datastax.tutorials.Test02_SpringData
```

**👁️ Expected output**

```bash
Categories:
- Clothing with children:[T-Shirts, Hoodies, Jackets]
- Cups and Mugs with children:[Cups, Coffee Mugs, Travel Mugs]
- Tech Accessories with children:[Mousepads, Wrist Rests, Laptop Covers]
- Wall Decor with children:[Posters, Wall Art]
```

✅ **8c: Working With Rest Controller**

`TestRestTemplate` is a neat way to test a web controller. The application will start on a random port with `@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)`

```bash
mvn test -Dtest=com.datastax.tutorials.Test03_RestController
```

**👁️ Expected output**

```bash
List Categories:
Clothing
Cups and Mugs
Tech Accessories
Wall Decor
```

[🏠 Back to Table of Contents](#-table-of-contents)

## 9. Start the Application

You can install the backend with the credentials using the following command

```
cd /workspace/workshop-ecommerce-app
mvn install -f backend/pom.xml -DskipTests
```

✅ **9a: Know your public URL**

The workshop application has opened with an ephemeral URL. To know the URL where your application endpoint will be exposed you can run the following command in the terminal after the build has completed. **Please note this URL and we will open this up in a new browser window if required later **.

```bash
gp url 8080
```
**👁️ Expected output**

![gitpod](data/img/gitpod-02-url.png?raw=true)

✅ **9b: Check APIs are not available (yet)**

Run the following command in the Gitpod terminal window

```
curl localhost:8080/api/v1/products/product/LS534S
```
**👁️ Expected output**

```
curl: (7) Failed to connect to localhost port 8080: Connection refused
```

Not to be overly concerned as we're going to be starting the application that will be served from the port.

✅ **9c: Start the application**

To start the application, we've provided a very simple convenience script that can be run as below.

```bash
./start.sh
```

Pay attention to popups being blocked as shown below and allow the popups.

![gitpod](data/img/PopupBlocked.png?raw=true)

You may encounter the following at different steps and although this may not be applicable right away, the steps are included **in advance** and summarized here so that you can keep an eye out for it. Different paths and different environments might be slightly different although Gitpod levels the playing field a bit.

Your e-commerce application should be up and running.

✅ **9d: Check APIs are now available**

Get back to Gitpod tab/window.

Leave the application running and open up another `shell` in the Gitpod terminal window by clicking on `+` and clicking on `bash` dropdown as shown below.

![gitpod](data/img/gitpod-newbash1.png?raw=true)

This will bring up a new `bash` shell as shown below.

![gitpod](data/img/gitpod-newbash2.png?raw=true)

Issue the following command in that shell as you did earlier.

```
curl localhost:8080/api/v1/products/product/LS534S
```

and you should see some output indicating that the API server is serving our ecommerce APIs.

**👁️ Expected output**

![gitpod](data/img/gitpod-newbash3.png?raw=true)

Try a few other APIs (**Hint: Look for the `RestController` java files in the respective sub-directories.**).

✅ **9e: OPTIONAL - Open in Gitpod preview window**

This might be useful for troubleshooting if your application does not automatically open up a browser tab.

If you want, you can run the following command to open your application in the preview window of Gitpod (it's much easier to use the app running in browser, though).

```
gp preview $(gp url 8080)
```

As indicated in the output below it's a very `Simple Browser`.

**👁️ Expected output**

![gitpod](data/img/gitpod-preview-1.png?raw=true)

If your application is running in the preview window but you have difficulty accessing it from the browser pay attention to popups being blocked by the browser as explained before.

✅ **9f: Get the Open API specification**

In the new shell window open the specification in the preview or browser with the following command

```
gp preview $(gp url 8080)/swagger-ui/index.html
```

The preview window looks like below. **It might help to close all the tabs or open this URL in a browser by clicking on the `open in browser` tab on the top right as shown**.

**👁️ Expected output**

![image](data/img/swagger2.png?raw=true)

Here's how it looks in the browser tab.

![image](data/img/swagger.png?raw=true)

This is the docs for the open APIs that enables the frontend or any other program to obtain the data and manipulate it with REST-based CRUD operations.

The complete app is running in the browser as shown below.

![image](data/img/splash.png?raw=true)

✅ **9g: Use your social login**

Hit login as shown below

![login](data/img/Oauthlogin0.png?raw=true)


You should be presented with the Google SSO Login option. Click on the icon as shown below.

![login](data/img/Oauthlogin1.png?raw=true)

Pick the Google user account and proceed to login as you would with Google.

![login](data/img/Oauthlogin2.png?raw=true)

If all the values are wired properly you should see the following screen with the icon above showing that the authentication worked as below and the `Logout` button now available.

![ouath](data/img/Oauthauthenticated.png?raw=true)

and voila, just like that we are done setting up user profile with Google. We can implement Github and other social logins similarly.

[🏠 Back to Table of Contents](#-table-of-contents)

✅ **9h: Process your order(s)**

Did you put items in your cart and check out?  You will likely have an order waiting in your "pending-orders" topic.  To simulate moving the orders between topics, a small Order Processor was created.  To build and run:

```
source .env
cd orderProcessor
mvn clean install
```

Once that process completes, have a look at the `target/` directory.  You should see a JAR named `ecom-0.0.1-SNAPSHOT-spring-boot.jar`.  To process an order on the "pending-orders" topic, you need to have it "picked."  To simulate a picking process, try this:

```
java -jar target/ecom-0.0.1-SNAPSHOT-spring-boot.jar pick
```

If an order is present, you should see the order JSON get processed and moved to the next topic:

```
{"cartId":"b8a5bd07-2337-44de-8890-582e88e29754","cartName":"b8a5bd07-2337-44de-8890-582e88e29754","orderId":"e8ecd3b0-498b-11ed-b5a7-fbd1f5143654","userId":"f1dbd2c0-bda4-4ccc-93dd-4aecd78758f5","productList":[{"productId":"DSS821XL","productName":"DataStax Gray Track Jacket","productQty":1,"productPrice":44.99},{"productId":"APC30XL","productName":"Apache Cassandra 3.0 Contributor T-Shirt","productQty":1,"productPrice":15.99}],"orderStatus":"PENDING","orderTimestamp":"Oct 11, 2022, 5:41:17 PM","orderSubtotal":60.98,"orderShippingHandling":4,"orderTax":3.05,"orderTotal":68.03,"shippingAddress":{"street":"123 Limon Gala Rd.","city":"Maple Grove","stateProvince":"Minnesota","postalCode":"55369","country":"United States"}}
Pushed order e8ecd3b0-498b-11ed-b5a7-fbd1f5143654 to ecommerce-aaron/default/picked-orders
```

[🏠 Back to Table of Contents](#-table-of-contents)

# Done?

Congratulations: you made it to the end of today's workshop. You will notice that the application is still incomplete as we're evolving it. More building to follow!!!

![Badge](data/img/build-an-ecommerce-app.png)

**... and see you at our next workshop!**

> Sincerely yours, The DataStax Developers
