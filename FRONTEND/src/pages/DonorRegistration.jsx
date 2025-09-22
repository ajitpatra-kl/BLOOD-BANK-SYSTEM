import React, { useState } from 'react';
import { Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { donorService } from '../services/apiService';

/**
 * Donor Registration page component
 */
const DonorRegistration = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [validated, setValidated] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    bloodGroup: '',
    age: '',
    weight: '',
    address: '',
    lastDonationDate: ''
  });

  const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    const form = e.currentTarget;
    if (form.checkValidity() === false) {
      e.stopPropagation();
      setValidated(true);
      return;
    }

    try {
      setLoading(true);
      setError(null);

      // Prepare data for submission
      const submitData = {
        ...formData,
        age: parseInt(formData.age),
        weight: parseFloat(formData.weight),
        lastDonationDate: formData.lastDonationDate || null
      };

      const response = await donorService.createDonor(submitData);
      
      if (response.success) {
        setSuccess(true);
        setFormData({
          name: '',
          email: '',
          phone: '',
          bloodGroup: '',
          age: '',
          weight: '',
          address: '',
          lastDonationDate: ''
        });
        setValidated(false);
        
        // Redirect to donor list after 2 seconds
        setTimeout(() => {
          navigate('/donors/list');
        }, 2000);
      }
    } catch (error) {
      console.error('Error registering donor:', error);
      setError(error.message || 'Failed to register donor. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {/* Page Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold text-danger">
            <i className="fas fa-user-plus me-2"></i>
            Donor Registration
          </h2>
          <p className="text-muted mb-0">Register as a blood donor and help save lives</p>
        </div>
        <Button 
          variant="outline-secondary"
          onClick={() => navigate('/donors/list')}
        >
          <i className="fas fa-list me-2"></i>
          View All Donors
        </Button>
      </div>

      <Row className="justify-content-center">
        <Col md={8}>
          <Card className="shadow-sm border-0">
            <Card.Header className="bg-danger text-white">
              <h5 className="mb-0">
                <i className="fas fa-heart me-2"></i>
                Donor Information
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {/* Success Alert */}
              {success && (
                <Alert variant="success" className="mb-4">
                  <i className="fas fa-check-circle me-2"></i>
                  <strong>Registration Successful!</strong> Thank you for registering as a donor. Redirecting to donor list...
                </Alert>
              )}

              {/* Error Alert */}
              {error && (
                <Alert variant="danger" dismissible onClose={() => setError(null)}>
                  <i className="fas fa-exclamation-circle me-2"></i>
                  {error}
                </Alert>
              )}

              {/* Registration Form */}
              <Form noValidate validated={validated} onSubmit={handleSubmit}>
                <Row>
                  {/* Name */}
                  <Col md={6} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-user me-1"></i>
                        Full Name *
                      </Form.Label>
                      <Form.Control
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                        minLength={2}
                        maxLength={100}
                        placeholder="Enter your full name"
                      />
                      <Form.Control.Feedback type="invalid">
                        Please provide a valid name (2-100 characters).
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  {/* Email */}
                  <Col md={6} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-envelope me-1"></i>
                        Email Address *
                      </Form.Label>
                      <Form.Control
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        placeholder="Enter your email address"
                      />
                      <Form.Control.Feedback type="invalid">
                        Please provide a valid email address.
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>
                </Row>

                <Row>
                  {/* Phone */}
                  <Col md={6} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-phone me-1"></i>
                        Phone Number *
                      </Form.Label>
                      <Form.Control
                        type="tel"
                        name="phone"
                        value={formData.phone}
                        onChange={handleChange}
                        required
                        pattern="^[+]?[0-9]{10,15}$"
                        placeholder="Enter your phone number"
                      />
                      <Form.Control.Feedback type="invalid">
                        Please provide a valid phone number (10-15 digits).
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  {/* Blood Group */}
                  <Col md={6} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-tint me-1"></i>
                        Blood Group *
                      </Form.Label>
                      <Form.Select
                        name="bloodGroup"
                        value={formData.bloodGroup}
                        onChange={handleChange}
                        required
                      >
                        <option value="">Select your blood group</option>
                        {bloodGroups.map(group => (
                          <option key={group} value={group}>{group}</option>
                        ))}
                      </Form.Select>
                      <Form.Control.Feedback type="invalid">
                        Please select your blood group.
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>
                </Row>

                <Row>
                  {/* Age */}
                  <Col md={4} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-calendar me-1"></i>
                        Age *
                      </Form.Label>
                      <Form.Control
                        type="number"
                        name="age"
                        value={formData.age}
                        onChange={handleChange}
                        required
                        min={18}
                        max={65}
                        placeholder="Age"
                      />
                      <Form.Control.Feedback type="invalid">
                        Age must be between 18 and 65 years.
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  {/* Weight */}
                  <Col md={4} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-weight me-1"></i>
                        Weight (kg) *
                      </Form.Label>
                      <Form.Control
                        type="number"
                        name="weight"
                        value={formData.weight}
                        onChange={handleChange}
                        required
                        min={50}
                        step={0.1}
                        placeholder="Weight in kg"
                      />
                      <Form.Control.Feedback type="invalid">
                        Weight must be at least 50 kg.
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>

                  {/* Last Donation Date */}
                  <Col md={4} className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-calendar-check me-1"></i>
                        Last Donation Date
                      </Form.Label>
                      <Form.Control
                        type="date"
                        name="lastDonationDate"
                        value={formData.lastDonationDate}
                        onChange={handleChange}
                        max={new Date().toISOString().split('T')[0]}
                      />
                      <Form.Text className="text-muted">
                        Leave blank if this is your first donation
                      </Form.Text>
                    </Form.Group>
                  </Col>
                </Row>

                {/* Address */}
                <Row>
                  <Col className="mb-3">
                    <Form.Group>
                      <Form.Label>
                        <i className="fas fa-map-marker-alt me-1"></i>
                        Address *
                      </Form.Label>
                      <Form.Control
                        as="textarea"
                        rows={3}
                        name="address"
                        value={formData.address}
                        onChange={handleChange}
                        required
                        maxLength={255}
                        placeholder="Enter your complete address"
                      />
                      <Form.Control.Feedback type="invalid">
                        Please provide your address.
                      </Form.Control.Feedback>
                    </Form.Group>
                  </Col>
                </Row>

                {/* Eligibility Information */}
                <div className="bg-light rounded p-3 mb-4">
                  <h6 className="text-primary mb-2">
                    <i className="fas fa-info-circle me-2"></i>
                    Donor Eligibility Criteria
                  </h6>
                  <Row className="small">
                    <Col md={6}>
                      <ul className="mb-0">
                        <li>Age: 18-65 years</li>
                        <li>Weight: Minimum 50 kg</li>
                        <li>Good general health</li>
                      </ul>
                    </Col>
                    <Col md={6}>
                      <ul className="mb-0">
                        <li>No recent illness or infection</li>
                        <li>56 days gap between donations</li>
                        <li>No high-risk activities</li>
                      </ul>
                    </Col>
                  </Row>
                </div>

                {/* Submit Button */}
                <div className="d-grid gap-2 d-md-flex justify-content-md-end">
                  <Button 
                    variant="outline-secondary" 
                    onClick={() => navigate('/')}
                    disabled={loading}
                  >
                    Cancel
                  </Button>
                  <Button 
                    type="submit" 
                    variant="danger" 
                    disabled={loading}
                    className="px-4"
                  >
                    {loading ? (
                      <>
                        <Spinner animation="border" size="sm" className="me-2" />
                        Registering...
                      </>
                    ) : (
                      <>
                        <i className="fas fa-save me-2"></i>
                        Register as Donor
                      </>
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DonorRegistration;