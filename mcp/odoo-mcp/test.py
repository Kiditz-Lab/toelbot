import asyncio

from async_odoo_client import AsyncOdooClient

async def main():
    client = AsyncOdooClient()
    await client.authenticate()
    result = await client.search_read("res.partner", [["is_company", "=", True]])
    print(result)
    await client.close()

asyncio.run(main())
