import React from 'react';
import { Navbar, Nav, Container, Badge } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

/**
 * Navigation component for the Blood Bank Management System
 */
const Navigation = () => {
  return (
    <Navbar bg="danger" variant="dark" expand="lg" className="shadow-sm">
      <Container>
        {/* Brand */}
        <LinkContainer to="/">
          <Navbar.Brand className="fw-bold">
            <i className="fas fa-tint me-2"></i>
            Blood Bank Management
          </Navbar.Brand>
        </LinkContainer>

        {/* Mobile toggle */}
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        
        {/* Navigation links */}
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            {/* Home */}
            <LinkContainer to="/">
              <Nav.Link>
                <i className="fas fa-home me-1"></i>
                Home
              </Nav.Link>
            </LinkContainer>

            {/* Donors */}
            <LinkContainer to="/donors/register">
              <Nav.Link>
                <i className="fas fa-user-plus me-1"></i>
                Register Donor
              </Nav.Link>
            </LinkContainer>

            <LinkContainer to="/donors/list">
              <Nav.Link>
                <i className="fas fa-users me-1"></i>
                Donors
              </Nav.Link>
            </LinkContainer>

            {/* Blood Inventory */}
            <LinkContainer to="/inventory">
              <Nav.Link>
                <i className="fas fa-flask me-1"></i>
                Blood Inventory
              </Nav.Link>
            </LinkContainer>

            {/* Blood Requests */}
            <LinkContainer to="/requests/new">
              <Nav.Link>
                <i className="fas fa-hand-holding-medical me-1"></i>
                Request Blood
              </Nav.Link>
            </LinkContainer>

            <LinkContainer to="/requests/status">
              <Nav.Link>
                <i className="fas fa-list-alt me-1"></i>
                Request Status
              </Nav.Link>
            </LinkContainer>

            {/* Admin */}
            <LinkContainer to="/admin">
              <Nav.Link>
                <i className="fas fa-cog me-1"></i>
                Admin
                <Badge bg="warning" text="dark" className="ms-1">
                  Admin
                </Badge>
              </Nav.Link>
            </LinkContainer>
          </Nav>

          {/* Right side links */}
          <Nav>
            <Nav.Link href="#" className="text-light">
              <i className="fas fa-bell me-1"></i>
              Notifications
            </Nav.Link>
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default Navigation;