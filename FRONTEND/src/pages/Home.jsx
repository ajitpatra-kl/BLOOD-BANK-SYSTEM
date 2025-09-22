import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Alert, Button, Badge, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { dashboardService } from '../services/apiService';

/**
 * Home page component with dashboard statistics
 */
const Home = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      setLoading(true);
      const response = await dashboardService.getDashboardStats();
      setStats(response.data);
      setError(null);
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      setError('Failed to load dashboard statistics');
      // Set default stats for demo purposes
      setStats({
        totalDonors: 0,
        eligibleDonors: 0,
        totalBloodUnits: 0,
        criticalShortages: 0,
        pendingRequests: 0,
        emergencyRequests: 0,
        todayRequests: 0,
        todayDonations: 0
      });
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="danger" />
        <p className="mt-3">Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div>
      {/* Hero Section */}
      <div className="bg-danger text-white rounded-3 p-4 mb-4">
        <Row className="align-items-center">
          <Col md={8}>
            <h1 className="display-4 fw-bold mb-3">
              <i className="fas fa-tint me-3"></i>
              Blood Bank Management System
            </h1>
            <p className="lead mb-4">
              Connecting donors with those in need. Every donation saves up to 3 lives.
            </p>
            <div className="d-flex gap-3 flex-wrap">
              <Button 
                variant="light" 
                size="lg"
                onClick={() => navigate('/donors/register')}
              >
                <i className="fas fa-user-plus me-2"></i>
                Become a Donor
              </Button>
              <Button 
                variant="outline-light" 
                size="lg"
                onClick={() => navigate('/requests/new')}
              >
                <i className="fas fa-hand-holding-medical me-2"></i>
                Request Blood
              </Button>
            </div>
          </Col>
          <Col md={4} className="text-center">
            <i className="fas fa-heartbeat display-1 opacity-75"></i>
          </Col>
        </Row>
      </div>

      {/* Error Alert */}
      {error && (
        <Alert variant="warning" dismissible onClose={() => setError(null)}>
          <i className="fas fa-exclamation-triangle me-2"></i>
          {error}
        </Alert>
      )}

      {/* Statistics Cards */}
      <Row className="g-4 mb-4">
        {/* Total Donors */}
        <Col md={3} sm={6}>
          <Card className="h-100 border-0 shadow-sm">
            <Card.Body className="text-center">
              <div className="text-primary mb-3">
                <i className="fas fa-users display-4"></i>
              </div>
              <h3 className="fw-bold text-primary">{stats?.totalDonors || 0}</h3>
              <p className="text-muted mb-0">Total Donors</p>
            </Card.Body>
          </Card>
        </Col>

        {/* Available Blood Units */}
        <Col md={3} sm={6}>
          <Card className="h-100 border-0 shadow-sm">
            <Card.Body className="text-center">
              <div className="text-success mb-3">
                <i className="fas fa-flask display-4"></i>
              </div>
              <h3 className="fw-bold text-success">{stats?.totalBloodUnits || 0}</h3>
              <p className="text-muted mb-0">Blood Units Available</p>
            </Card.Body>
          </Card>
        </Col>

        {/* Pending Requests */}
        <Col md={3} sm={6}>
          <Card className="h-100 border-0 shadow-sm">
            <Card.Body className="text-center">
              <div className="text-warning mb-3">
                <i className="fas fa-clock display-4"></i>
              </div>
              <h3 className="fw-bold text-warning">{stats?.pendingRequests || 0}</h3>
              <p className="text-muted mb-0">Pending Requests</p>
            </Card.Body>
          </Card>
        </Col>

        {/* Critical Shortages */}
        <Col md={3} sm={6}>
          <Card className="h-100 border-0 shadow-sm">
            <Card.Body className="text-center">
              <div className="text-danger mb-3">
                <i className="fas fa-exclamation-triangle display-4"></i>
              </div>
              <h3 className="fw-bold text-danger">{stats?.criticalShortages || 0}</h3>
              <p className="text-muted mb-0">Critical Shortages</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Emergency Alert */}
      {stats?.emergencyRequests > 0 && (
        <Alert variant="danger" className="mb-4">
          <Alert.Heading>
            <i className="fas fa-exclamation-circle me-2"></i>
            Emergency Requests
          </Alert.Heading>
          <p>
            There are <strong>{stats.emergencyRequests}</strong> emergency blood requests that need immediate attention.
          </p>
          <Button variant="danger" onClick={() => navigate('/admin')}>
            View Emergency Requests
          </Button>
        </Alert>
      )}

      {/* Quick Actions */}
      <Row className="g-4 mb-4">
        <Col md={6}>
          <Card className="h-100 border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">
                <i className="fas fa-tachometer-alt me-2"></i>
                Quick Actions
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="d-grid gap-2">
                <Button 
                  variant="outline-primary"
                  onClick={() => navigate('/donors/register')}
                >
                  <i className="fas fa-user-plus me-2"></i>
                  Register New Donor
                </Button>
                <Button 
                  variant="outline-success"
                  onClick={() => navigate('/inventory')}
                >
                  <i className="fas fa-flask me-2"></i>
                  Manage Blood Inventory
                </Button>
                <Button 
                  variant="outline-warning"
                  onClick={() => navigate('/requests/new')}
                >
                  <i className="fas fa-hand-holding-medical me-2"></i>
                  Create Blood Request
                </Button>
                <Button 
                  variant="outline-info"
                  onClick={() => navigate('/requests/status')}
                >
                  <i className="fas fa-list-alt me-2"></i>
                  Check Request Status
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>

        <Col md={6}>
          <Card className="h-100 border-0 shadow-sm">
            <Card.Header className="bg-light">
              <h5 className="mb-0">
                <i className="fas fa-chart-line me-2"></i>
                Today's Summary
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="row text-center">
                <div className="col-6">
                  <h4 className="text-primary">{stats?.todayDonations || 0}</h4>
                  <small className="text-muted">Donations Today</small>
                </div>
                <div className="col-6">
                  <h4 className="text-warning">{stats?.todayRequests || 0}</h4>
                  <small className="text-muted">Requests Today</small>
                </div>
              </div>
              <hr />
              <div className="row text-center">
                <div className="col-6">
                  <h4 className="text-success">{stats?.eligibleDonors || 0}</h4>
                  <small className="text-muted">Eligible Donors</small>
                </div>
                <div className="col-6">
                  <Badge bg="danger" className="fs-6">
                    {stats?.criticalShortages || 0} Critical
                  </Badge>
                  <br />
                  <small className="text-muted">Blood Group Shortages</small>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Information Section */}
      <Card className="border-0 shadow-sm">
        <Card.Header className="bg-light">
          <h5 className="mb-0">
            <i className="fas fa-info-circle me-2"></i>
            Important Information
          </h5>
        </Card.Header>
        <Card.Body>
          <Row>
            <Col md={4}>
              <h6 className="text-primary">
                <i className="fas fa-user-md me-2"></i>
                Donation Eligibility
              </h6>
              <ul className="small">
                <li>Age: 18-65 years</li>
                <li>Weight: Minimum 50 kg</li>
                <li>Good health condition</li>
                <li>56 days gap between donations</li>
              </ul>
            </Col>
            <Col md={4}>
              <h6 className="text-success">
                <i className="fas fa-flask me-2"></i>
                Blood Types Needed
              </h6>
              <ul className="small">
                <li>O- (Universal donor)</li>
                <li>O+ (Most common)</li>
                <li>A-, B-, AB- (Rare types)</li>
                <li>All types always welcome</li>
              </ul>
            </Col>
            <Col md={4}>
              <h6 className="text-warning">
                <i className="fas fa-clock me-2"></i>
                Operating Hours
              </h6>
              <ul className="small">
                <li>Monday - Friday: 8:00 AM - 6:00 PM</li>
                <li>Saturday: 9:00 AM - 4:00 PM</li>
                <li>Sunday: Closed</li>
                <li>Emergency: 24/7 available</li>
              </ul>
            </Col>
          </Row>
        </Card.Body>
      </Card>
    </div>
  );
};

export default Home;