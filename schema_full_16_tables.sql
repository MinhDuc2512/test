-- ============================================================
-- HỆ THỐNG TRA CỨU & THEO DÕI VẬN ĐƠN (SHIPMENT TRACKING SYSTEM)
-- POSTGRESQL FULL DATABASE DDL SCRIPT (16 BẢNG CHUẨN)
-- ============================================================

-- ------------------------------------------------------------
-- NHÓM 1: NGƯỜI DÙNG & PHÂN QUYỀN
-- ------------------------------------------------------------

-- 1. Bảng users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'CUSTOMER', -- CUSTOMER, SHIPPER, WAREHOUSE_STAFF, CSKH, ADMIN, PARTNER
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, LOCKED, DELETED
    assigned_post_office_id BIGINT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng user_addresses
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    label VARCHAR(50),
    contact_name VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    ward VARCHAR(100) NOT NULL,
    detail_address VARCHAR(255) NOT NULL,
    lat DECIMAL(10, 7),
    lng DECIMAL(10, 7),
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Bảng api_keys
CREATE TABLE IF NOT EXISTS api_keys (
    id BIGSERIAL PRIMARY KEY,
    partner_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    key_hash VARCHAR(255) NOT NULL UNIQUE,
    permissions VARCHAR(255) NOT NULL DEFAULT 'CREATE_ORDER,TRACK',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- NHÓM 2: HẠ TẦNG VẬN HÀNH
-- ------------------------------------------------------------

-- 4. Bảng post_offices
CREATE TABLE IF NOT EXISTS post_offices (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(30) NOT NULL, -- SORT_HUB, BRANCH, PICKUP_POINT
    province VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    ward VARCHAR(100),
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    lat DECIMAL(10, 7),
    lng DECIMAL(10, 7),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

-- Liên kết FK cho assigned_post_office_id trong bảng users
ALTER TABLE users ADD CONSTRAINT fk_users_post_office FOREIGN KEY (assigned_post_office_id) REFERENCES post_offices(id) ON DELETE SET NULL;

-- 5. Bảng service_types
CREATE TABLE IF NOT EXISTS service_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    max_weight_kg DECIMAL(8, 2),
    sla_hours INT,
    description VARCHAR(255)
);

-- 6. Bảng pricing_zones
CREATE TABLE IF NOT EXISTS pricing_zones (
    id BIGSERIAL PRIMARY KEY,
    from_province VARCHAR(100) NOT NULL,
    to_province VARCHAR(100) NOT NULL,
    zone_tier VARCHAR(30) NOT NULL, -- INNER_CITY, SAME_REGION, CROSS_REGION, REMOTE
    CONSTRAINT uq_zone UNIQUE (from_province, to_province)
);

-- 7. Bảng tariffs
CREATE TABLE IF NOT EXISTS tariffs (
    id BIGSERIAL PRIMARY KEY,
    service_type_id BIGINT NOT NULL REFERENCES service_types(id) ON DELETE CASCADE,
    zone_tier VARCHAR(30) NOT NULL,
    weight_from_kg DECIMAL(8, 2) NOT NULL,
    weight_to_kg DECIMAL(8, 2) NOT NULL,
    base_price DECIMAL(12, 0) NOT NULL,
    price_per_extra_kg DECIMAL(12, 0) NOT NULL DEFAULT 0
);

-- ------------------------------------------------------------
-- NHÓM 3: LÕI ĐƠN HÀNG (SHIPMENTS)
-- ------------------------------------------------------------

-- 8. Bảng shipments
CREATE TABLE IF NOT EXISTS shipments (
    id BIGSERIAL PRIMARY KEY,
    tracking_code VARCHAR(30) NOT NULL UNIQUE,
    sender_id BIGINT NULL REFERENCES users(id) ON DELETE SET NULL,
    sender_name VARCHAR(150) NOT NULL,
    sender_phone VARCHAR(20) NOT NULL,
    sender_address VARCHAR(255) NOT NULL,
    sender_province VARCHAR(100) NOT NULL,
    sender_district VARCHAR(100) NOT NULL,
    sender_ward VARCHAR(100),
    receiver_name VARCHAR(150) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    receiver_province VARCHAR(100) NOT NULL,
    receiver_district VARCHAR(100) NOT NULL,
    receiver_ward VARCHAR(100),
    service_type_id BIGINT NULL REFERENCES service_types(id) ON DELETE SET NULL,
    weight_kg DECIMAL(8, 2) NOT NULL,
    length_cm DECIMAL(8, 2),
    width_cm DECIMAL(8, 2),
    height_cm DECIMAL(8, 2),
    declared_value DECIMAL(14, 0) DEFAULT 0,
    item_note VARCHAR(255),
    cod_amount DECIMAL(14, 0) DEFAULT 0,
    cod_payer VARCHAR(20) DEFAULT 'RECEIVER', -- SENDER, RECEIVER
    shipping_fee DECIMAL(12, 0) NOT NULL,
    insurance_fee DECIMAL(12, 0) DEFAULT 0,
    total_fee DECIMAL(12, 0) NOT NULL,
    current_status_code VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    current_post_office_id BIGINT NULL REFERENCES post_offices(id) ON DELETE SET NULL,
    origin_post_office_id BIGINT NULL REFERENCES post_offices(id) ON DELETE SET NULL,
    destination_post_office_id BIGINT NULL REFERENCES post_offices(id) ON DELETE SET NULL,
    assigned_shipper_id BIGINT NULL REFERENCES users(id) ON DELETE SET NULL,
    source VARCHAR(20) NOT NULL DEFAULT 'WEB', -- WEB, APP, API_SHOPEE, API_LAZADA, API_TIKTOK, ADMIN
    partner_order_id VARCHAR(100) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(current_status_code);
CREATE INDEX IF NOT EXISTS idx_shipments_shipper ON shipments(assigned_shipper_id);
CREATE INDEX IF NOT EXISTS idx_shipments_created ON shipments(created_at);

-- ------------------------------------------------------------
-- NHÓM 4: LÕI TRACKING (QUAN TRỌNG NHẤT)
-- ------------------------------------------------------------

-- 9. Bảng status_codes
CREATE TABLE IF NOT EXISTS status_codes (
    code VARCHAR(30) PRIMARY KEY,
    name_vi VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    is_final BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL
);

-- Liên kết FK cho current_status_code trong bảng shipments
ALTER TABLE shipments ADD CONSTRAINT fk_shipments_status_code FOREIGN KEY (current_status_code) REFERENCES status_codes(code);

-- 10. Bảng shipment_status_history (APPEND-ONLY)
CREATE TABLE IF NOT EXISTS shipment_status_history (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    status_code VARCHAR(30) NOT NULL REFERENCES status_codes(code),
    location_post_office_id BIGINT NULL REFERENCES post_offices(id) ON DELETE SET NULL,
    lat DECIMAL(10, 7) NULL,
    lng DECIMAL(10, 7) NULL,
    note VARCHAR(255),
    updated_by_user_id BIGINT NULL REFERENCES users(id) ON DELETE SET NULL,
    update_source VARCHAR(20) NOT NULL DEFAULT 'APP', -- SCAN, APP, WEBHOOK, ADMIN, SYSTEM
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_history_shipment_time ON shipment_status_history(shipment_id, created_at);

-- ------------------------------------------------------------
-- NHÓM 5: GIAO HÀNG & COD
-- ------------------------------------------------------------

-- 11. Bảng delivery_attempts
CREATE TABLE IF NOT EXISTS delivery_attempts (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    attempt_no INT NOT NULL,
    shipper_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    result VARCHAR(20) NOT NULL, -- SUCCESS, FAILED
    fail_reason VARCHAR(255) NULL,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_attempt UNIQUE (shipment_id, attempt_no)
);

-- 12. Bảng cod_transactions
CREATE TABLE IF NOT EXISTS cod_transactions (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL UNIQUE REFERENCES shipments(id) ON DELETE CASCADE,
    amount DECIMAL(14, 0) NOT NULL,
    collected_by_shipper_id BIGINT NULL REFERENCES users(id) ON DELETE SET NULL,
    collected_at TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, COLLECTED, RECONCILED, TRANSFERRED
    reconciled_at TIMESTAMP NULL,
    transferred_at TIMESTAMP NULL
);

-- 13. Bảng shipment_images
CREATE TABLE IF NOT EXISTS shipment_images (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    delivery_attempt_id BIGINT NULL REFERENCES delivery_attempts(id) ON DELETE SET NULL,
    image_url VARCHAR(500) NOT NULL,
    type VARCHAR(30) NOT NULL, -- PROOF_OF_DELIVERY, DAMAGE_EVIDENCE, PACKAGE_PHOTO
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- NHÓM 6: THÔNG BÁO, KHIẾU NẠI & TÍCH HỢP
-- ------------------------------------------------------------

-- 14. Bảng notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NULL REFERENCES users(id) ON DELETE CASCADE,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    channel VARCHAR(20) NOT NULL, -- SMS, EMAIL, PUSH
    content VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 15. Bảng complaints
CREATE TABLE IF NOT EXISTS complaints (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT NOT NULL REFERENCES shipments(id) ON DELETE CASCADE,
    created_by_user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(30) NOT NULL, -- LOST, DAMAGED, DELAYED, WRONG_COD, OTHER
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, RESOLVED, CLOSED
    assigned_to_user_id BIGINT NULL REFERENCES users(id) ON DELETE SET NULL,
    resolution_note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL
);

-- 16. Bảng partner_webhooks_log
CREATE TABLE IF NOT EXISTS partner_webhooks_log (
    id BIGSERIAL PRIMARY KEY,
    partner_name VARCHAR(100) NOT NULL,
    direction VARCHAR(20) NOT NULL, -- INBOUND, OUTBOUND
    shipment_id BIGINT NULL REFERENCES shipments(id) ON DELETE SET NULL,
    payload JSONB NOT NULL,
    http_status INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- SEED DATA MẪU: DANH MỤC 12 TRẠNG THÁI VẬN ĐƠN CHUẨN
-- ------------------------------------------------------------
INSERT INTO status_codes (code, name_vi, name_en, is_final, sort_order) VALUES
('CREATED', 'Đã tạo đơn', 'Order created', FALSE, 1),
('PICKUP_PENDING', 'Chờ lấy hàng', 'Pending pickup', FALSE, 2),
('PICKED_UP', 'Đã lấy hàng', 'Picked up', FALSE, 3),
('AT_SORT_HUB', 'Đã nhập kho phân loại', 'At sorting hub', FALSE, 4),
('IN_TRANSIT', 'Đang trung chuyển', 'In transit', FALSE, 5),
('ARRIVED_DEST_HUB', 'Đã đến bưu cục khu vực', 'Arrived at destination', FALSE, 6),
('OUT_FOR_DELIVERY', 'Đang giao hàng', 'Out for delivery', FALSE, 7),
('DELIVERED', 'Giao hàng thành công', 'Delivered', TRUE, 8),
('DELIVERY_FAILED', 'Giao hàng thất bại', 'Delivery failed', FALSE, 9),
('RETURNING', 'Đang hoàn hàng', 'Returning to sender', FALSE, 10),
('RETURNED', 'Đã hoàn hàng', 'Returned to sender', TRUE, 11),
('CANCELLED', 'Đã hủy đơn', 'Cancelled', TRUE, 12)
ON CONFLICT (code) DO NOTHING;
