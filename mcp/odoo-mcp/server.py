from async_odoo_client import AsyncOdooClient
from typing import List
from mcp.server.fastmcp import FastMCP


mcp = FastMCP("Odoo", dependencies=["aiohttp", "aioxmlrpc"])

odoo = AsyncOdooClient()

@mcp.tool(description='Search for products by name and limit')
async def get_products(name: str) -> list[dict]:
    domain = [('name', 'ilike', name)] if name else []
    results = await odoo.search_read(
        model='product.template',
        domain=domain,
        fields=['id', 'name', 'default_code', 'list_price'],
        limit=20
    )

    return results

if __name__ == "__main__":
    mcp.run(transport='stdio')
    
    

 