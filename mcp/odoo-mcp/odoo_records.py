from async_odoo_client import AsyncOdooClient
from mcp.server.fastmcp import FastMCP

# Initialize FastMCP
mcp = FastMCP(
    "odoo-records",
    dependencies=[
        'aiohttp', 'aiohttp_xmlrpc'
    ],
)

# Tool for Searching Odoo Records
@mcp.tool(description="Search records in Odoo based on provided model, domain, and fields")
async def search_odoo(
    model: str,
    domain: list,
    fields: list[str]
) -> list[dict]:
    client = AsyncOdooClient()
    await client.authenticate()
    records = await client.search_read(model, domain, fields)
    await client.close()
    return records


# Tool for Creating an Odoo Record
@mcp.tool(description="Create a new record in Odoo")
async def create_odoo_record(
    model: str,
    data: dict
) -> int:
    client = AsyncOdooClient()
    await client.authenticate()
    record_id = await client.create(model, data)
    await client.close()
    return record_id


# Tool for Updating an Odoo Record
@mcp.tool(description="Update an existing record in Odoo")
async def update_odoo_record(
    model: str,
    record_id: int,
    data: dict
) -> bool:
    client = AsyncOdooClient()
    await client.authenticate()
    success = await client.write(model, [record_id], data)
    await client.close()
    return success


# Tool for Deleting an Odoo Record
@mcp.tool(description="Delete a record in Odoo")
async def delete_odoo_record(
    model: str,
    record_id: int
) -> bool:
    client = AsyncOdooClient()
    await client.authenticate()
    success = await client.unlink(model, [record_id])
    await client.close()
    return success


# Tool for Executing Arbitrary Odoo Method (Execute_kw)
@mcp.tool(description="Execute a method on an Odoo model with arguments and keyword arguments")
async def execute_odoo_method(
    model: str,
    method: str,
    args: list,
    kwargs: dict
) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()
    result = await client.execute_kw(
        model,
        method,
        *args,
        **kwargs
    )
    await client.close()
    return result


# Tool for Fetching Odoo Fields Metadata
@mcp.tool(description="Get field metadata of a specific model in Odoo")
async def get_odoo_fields(model: str) -> dict:
    client = AsyncOdooClient()
    await client.authenticate()
    fields = await client.fields_get(model)
    await client.close()
    return fields
