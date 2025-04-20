# Shopify Overview

**Shopify** is a complete commerce platform that allows you to start, grow, and manage a business.  
It lets you create an online store to sell products, manage inventory, accept payments, and handle shipping — all from a single system.

## Key Features
- **Online Store**: Build and customize an eCommerce website.
- **Sales Channels**: Sell through websites, social media, online marketplaces, and in-person with POS.
- **Product Management**: Add, organize, and manage products easily.
- **Order Management**: Track, process, and fulfill customer orders.
- **Customer Management**: Access detailed customer profiles and purchase history.
- **Marketing Tools**: SEO, discount codes, abandoned cart recovery, email marketing, and more.
- **Analytics**: View detailed reports about sales, customers, and store performance.
- **Third-Party Apps**: Extend Shopify’s functionality by integrating apps from the Shopify App Store.
- **Custom Apps**: Build private apps for personalized needs using Shopify APIs.

---

# Shopify Setup

If you are integrating Shopify into our app (for example, to fetch products, customers, and orders), you will typically create a **Custom App** inside Shopify.

## Creating a Custom App

Follow these steps carefully:

1. **Login to Shopify Admin**
   - Go to your store’s admin dashboard.

2. **Navigate to Apps and Sales Channels**
   - Click on **Settings** → **Apps and sales channels**.

3. **Enable App Development**
   - Click **Develop apps**.
   - If required, enable **App Development** (Shopify may ask for confirmation).

4. **Create Your App**
   - Click **Create an app**.
   - Enter a name for your app, e.g., **Shopify MCP Server**.
   - Choose a developer or yourself as the app owner.

5. **Configure Admin API Scopes**
   - Click **Configure Admin API scopes**.
   - Select these scopes:
     - `read_products`
     - `write_products`
     - `read_customers`
     - `write_customers`
     - `read_orders`
     - `write_orders`

6. **Save Settings**
   - After selecting the scopes, click **Save**.

7. **Install the App**
   - Click **Install app**.
   - Confirm by clicking **Install** when prompted.

8. **Get API Credentials**
   - After the app is installed, you’ll find your **Admin API access token**.
   - **Important**: Copy and securely store the **Admin API access token** — you won't be able to see it again later.

---

# How You Interact

| Area | Method Name | What It Does | How User Could Talk |
|:---|:---|:---|:---|
| **Products** | `list_products(limit)` | List products, with optional limit | "Show me the latest 5 products" |
|  | `search_products(query, limit)` | Search products by title | "Find products about 'shoes'" |
|  | `get_product(product_id)` | Get details of a product by ID | "Tell me details of product 123456" |
|  | `create_product(product_data)` | Create a new product | "Add a new T-shirt product" |
|  | `update_product(product_id, update_data)` | Update product details | "Change the price of product 123456" |
|  | `delete_product(product_id)` | Delete a product | "Delete product 123456" |
| **Orders** | `list_orders(limit)` | List orders | "Show me recent 10 orders" |
|  | `get_order(order_id)` | Get order details | "Show me order 987654" |
| **Customers** | `list_customers(limit)` | List customers | "List my first 10 customers" |
|  | `get_customer(customer_id)` | Get customer by ID | "Show details for customer 123" |
|  | `create_customer(customer_data)` | Create a new customer | "Add a new customer" |
|  | `update_customer(customer_id, update_data)` | Update a customer | "Change email of customer 123" |
|  | `delete_customer(customer_id)` | Delete a customer | "Remove customer 123" |
| **Collections** | `list_custom_collections(limit)` | List collections | "List product collections" |
|  | `create_custom_collection(collection_data)` | Create collection | "Create a summer sale collection" |
| **Metafields** | `list_metafields(limit)` | List metafields | "Show me 10 metafields" |
| **Inventory** | `list_inventory_items(limit)` | List inventory items | "List inventory stock items" |
| **Discounts** | `list_price_rules(limit)` | List price rules (discount templates) | "Show my discount rules" |
|  | `create_price_rule(price_rule_data)` | Create a price rule | "Create a 20% off discount rule" |
|  | `create_discount_code(price_rule_id, discount_data)` | Create a discount code inside a price rule | "Create discount code 'SUMMER20' under that rule" |
| **Generic** | `custom_shopify_request(method, endpoint, params, data)` | Make any API call to Shopify | "Make a custom API request" (for advanced use) |

---

> 💬 **Tip:** Users can speak naturally to trigger the right methods automatically!
