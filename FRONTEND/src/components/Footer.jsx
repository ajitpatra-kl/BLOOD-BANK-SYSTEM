import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';

/**
 * Footer component for the Blood Bank Management System
 */
const Footer = () => {
  return (
    <footer className="bg-dark text-light py-4 mt-auto">
      <Container>
        <Row>
          <Col md={6}>
            <h5 className="fw-bold">
              <i className="fas fa-tint me-2 text-danger"></i>
              Blood Bank Management System
            </h5>
            <p className="mb-2">
              Saving lives through efficient blood donation and distribution management.
            </p>
            <small className="text-muted">
              © {new Date().getFullYear()} Blood Bank Management System. All rights reserved.
            </small>
          </Col>
          <Col md={3}>
            <h6 className="fw-bold">Quick Links</h6>
            <ul className="list-unstyled">
              <li><a href="/donors/register" className="text-light text-decoration-none">Register as Donor</a></li>
              <li><a href="/requests/new" className="text-light text-decoration-none">Request Blood</a></li>
              <li><a href="/inventory" className="text-light text-decoration-none">Blood Inventory</a></li>
              <li><a href="/admin" className="text-light text-decoration-none">Admin Panel</a></li>
            </ul>
          </Col>
          <Col md={3}>
            <h6 className="fw-bold">Emergency Contact</h6>
            <p className="mb-1">
              <i className="fas fa-phone me-2"></i>
              +1 (555) 123-4567
            </p>
            <p className="mb-1">
              <i className="fas fa-envelope me-2"></i>
              emergency@bloodbank.org
            </p>
            <p className="mb-1">
              <i className="fas fa-clock me-2"></i>
              24/7 Emergency Service
            </p>
          </Col>
        </Row>
        <hr className="my-3" />
        <Row>
          <Col className="text-center">
            <small className="text-muted">
              Built with <i className="fas fa-heart text-danger"></i> for humanity
            </small>
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;