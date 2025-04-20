from pydantic import BaseModel
from typing import List, Optional


class ProductVariant(BaseModel):
    option1: str
    price: str
    sku: Optional[str] = None

class ProductImage(BaseModel):
    src: str

class ProductCreate(BaseModel):
    title: str
    body_html: Optional[str] = None
    vendor: Optional[str] = None
    product_type: Optional[str] = None
    tags: Optional[List[str]] = None
    variants: Optional[List[ProductVariant]] = None
    images: Optional[List[ProductImage]] = None

class ProductVariantUpdate(BaseModel):
    id: Optional[int] = None  # For updating existing variant
    option1: Optional[str] = None
    price: Optional[str] = None
    sku: Optional[str] = None

class ProductImageUpdate(BaseModel):
    id: Optional[int] = None  # Optional for updating existing image
    src: Optional[str] = None

class ProductUpdate(BaseModel):
    title: Optional[str] = None
    body_html: Optional[str] = None
    vendor: Optional[str] = None
    product_type: Optional[str] = None
    tags: Optional[List[str]] = None
    variants: Optional[List[ProductVariantUpdate]] = None
    images: Optional[List[ProductImageUpdate]] = None

from pydantic import BaseModel, EmailStr
from typing import Optional, List


class Address(BaseModel):
    address1: Optional[str] = None
    address2: Optional[str] = None
    city: Optional[str] = None
    province: Optional[str] = None
    country: Optional[str] = None
    zip: Optional[str] = None
    phone: Optional[str] = None
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    company: Optional[str] = None

class CustomerCreate(BaseModel):
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    email: EmailStr
    phone: Optional[str] = None
    verified_email: Optional[bool] = None
    addresses: Optional[List[Address]] = None
    tags: Optional[str] = None
    note: Optional[str] = None
    accepts_marketing: Optional[bool] = None

class CustomerUpdate(BaseModel):
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    email: Optional[EmailStr] = None
    phone: Optional[str] = None
    verified_email: Optional[bool] = None
    addresses: Optional[List[Address]] = None
    tags: Optional[str] = None
    note: Optional[str] = None
    accepts_marketing: Optional[bool] = None
