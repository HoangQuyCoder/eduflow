CREATE TABLE enrollments (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  course_id UUID NOT NULL,
  enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(50) DEFAULT 'ACTIVE',
  progress_percent INT DEFAULT 0,
  completed_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(user_id, course_id)
);

CREATE TABLE lesson_progress (
  id UUID PRIMARY KEY,
  enrollment_id UUID NOT NULL REFERENCES enrollments(id),
  lesson_id UUID NOT NULL,
  is_completed BOOLEAN DEFAULT false,
  completed_at TIMESTAMP,
  time_spent_minutes INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(enrollment_id, lesson_id)
);

CREATE TABLE payments (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  course_id UUID NOT NULL,
  enrollment_id UUID NOT NULL REFERENCES enrollments(id),
  amount DECIMAL(10, 2) NOT NULL,
  currency VARCHAR(3) DEFAULT 'USD',
  payment_method VARCHAR(50),
  status VARCHAR(50) DEFAULT 'PENDING',
  transaction_id VARCHAR(255),
  paid_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_enrollments ON enrollments(user_id);
CREATE INDEX idx_course_enrollments ON enrollments(course_id);
