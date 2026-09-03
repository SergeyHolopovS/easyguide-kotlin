CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(32),
    avatar_url VARCHAR(255),
    is_guide BOOLEAN NOT NULL DEFAULT FALSE,
    bio TEXT,
    city VARCHAR(255),
    languages TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE tours (
    id UUID PRIMARY KEY,
    guide_id UUID NOT NULL REFERENCES users (id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    city VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    meeting_point VARCHAR(255) NOT NULL,
    timezone VARCHAR(64) NOT NULL,
    duration_minutes INT NOT NULL,
    price NUMERIC(10, 2),
    max_people INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    rating DOUBLE PRECISION,
    reviews_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE tour_photos (
    id UUID PRIMARY KEY,
    tour_id UUID NOT NULL REFERENCES tours (id) ON DELETE CASCADE,
    url VARCHAR(1024) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE tour_slots (
    id UUID PRIMARY KEY,
    tour_id UUID NOT NULL REFERENCES tours (id) ON DELETE CASCADE,
    starts_at TIMESTAMPTZ NOT NULL,
    capacity INT NOT NULL,
    booked_seats INT NOT NULL DEFAULT 0,
    is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tour_id, starts_at)
);

CREATE TABLE bookings (
    id UUID PRIMARY KEY,
    slot_id UUID NOT NULL REFERENCES tour_slots (id),
    user_id UUID NOT NULL REFERENCES users (id),
    seats INT NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    contact_phone VARCHAR(32),
    comment TEXT,
    status VARCHAR(50) NOT NULL,
    cancel_reason TEXT,
    cancelled_by VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    booking_id UUID NOT NULL REFERENCES bookings (id) UNIQUE,
    tour_id UUID NOT NULL REFERENCES tours (id),
    author_id UUID NOT NULL REFERENCES users (id),
    rating INT NOT NULL,
    text TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tours_city_status ON tours (city, status);
CREATE INDEX idx_tours_guide_id ON tours (guide_id);
CREATE INDEX idx_bookings_user_id_status ON bookings (user_id, status);
CREATE INDEX idx_bookings_slot_id ON bookings (slot_id);
