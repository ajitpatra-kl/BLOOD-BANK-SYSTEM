import React, { useState } from 'react';
import { Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { bloodRequestService } from '../services/apiService';

/**
 * Blood Request page component
 */
const BloodRequest = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const [validated, setValidated] = useState(false);

  const [formData, setFormData] = useState({
    requesterName: '',
    contactEmail: '',
    contactPhone: '',
    bloodGroup: '',
    unitsRequested: 1,
    urgencyLevel: 'NORMAL',
    hospitalName: '',
    patientName: '',
    medicalReason: ''
  });

  const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];
  const urgencyLevels = [
    { value: 'NORMAL', label: 'Normal', color: 'success' },
    { value: 'URGENT', label: 'Urgent', color: 'warning' },
    { value: 'EMERGENCY', label: 'Emergency', color: 'danger' }
  ];

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
        unitsRequested: parseInt(formData.unitsRequested)
      };

      const response = await bloodRequestService.createRequest(submitData);
      
      if (response.success) {
        setSuccess(true);
        setFormData({
          requesterName: '',
          contactEmail: '',
          contactPhone: '',
          bloodGroup: '',
          unitsRequested: 1,
          urgencyLevel: 'NORMAL',
          hospitalName: '',
          patientName: '',
          medicalReason: ''
        });
        setValidated(false);
        
        // Redirect to request status after 3 seconds
        setTimeout(() => {
          navigate('/requests/status');
        }, 3000);
      }
    } catch (error) {
      console.error('Error creating blood request:', error);
      setError(error.message || 'Failed to create blood request. Please try again.');
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
            <i className="fas fa-hand-holding-medical me-2"></i>
            Blood Request
          </h2>
          <p className="text-muted mb-0">Request blood units for medical needs</p>
        </div>
        <Button 
          variant="outline-secondary"
          onClick={() => navigate('/requests/status')}
        >
          <i className="fas fa-list-alt me-2"></i>
          View Request Status
        </Button>
      </div>

      <Row className="justify-content-center">
        <Col md={10}>
          <Card className="shadow-sm border-0">
            <Card.Header className="bg-danger text-white">
              <h5 className="mb-0">
                <i className="fas fa-file-medical me-2"></i>
                Blood Request Form
              </h5>
            </Card.Header>
            <Card.Body className="p-4">
              {/* Success Alert */}
              {success && (
                <Alert variant="success" className="mb-4">
                  <i className="fas fa-check-circle me-2"></i>
                  <strong>Request Submitted Successfully!</strong> Your blood request has been submitted and is pending review. Redirecting to status page...
                </Alert>
              )}

              {/* Error Alert */}
              {error && (
                <Alert variant="danger" dismissible onClose={() => setError(null)}>
                  <i className="fas fa-exclamation-circle me-2"></i>
                  {error}
                </Alert>
              )}

              {/* Request Form */}
              <Form noValidate validated={validated} onSubmit={handleSubmit}>
                {/* Requester Information */}
                <Card className="mb-4 border-light">
                  <Card.Header className="bg-light">
                    <h6 className="mb-0 text-primary">
                      <i className="fas fa-user me-2"></i>
                      Requester Information
                    </h6>
                  </Card.Header>
                  <Card.Body>
                    <Row>
                      <Col md={6} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-user me-1"></i>
                            Requester Name *
                          </Form.Label>
                          <Form.Control
                            type="text"
                            name="requesterName"
                            value={formData.requesterName}
                            onChange={handleChange}
                            required
                            minLength={2}
                            maxLength={100}
                            placeholder="Enter requester's full name"
                          />
                          <Form.Control.Feedback type="invalid">
                            Please provide a valid name (2-100 characters).
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                      <Col md={6} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-envelope me-1"></i>
                            Contact Email *
                          </Form.Label>
                          <Form.Control
                            type="email"
                            name="contactEmail"
                            value={formData.contactEmail}
                            onChange={handleChange}
                            required
                            placeholder="Enter contact email address"
                          />
                          <Form.Control.Feedback type="invalid">
                            Please provide a valid email address.
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                    </Row>

                    <Row>
                      <Col md={6} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-phone me-1"></i>
                            Contact Phone *
                          </Form.Label>
                          <Form.Control
                            type="tel"
                            name="contactPhone"
                            value={formData.contactPhone}
                            onChange={handleChange}
                            required
                            pattern="^[+]?[0-9]{10,15}$"
                            placeholder="Enter contact phone number"
                          />
                          <Form.Control.Feedback type="invalid">
                            Please provide a valid phone number (10-15 digits).
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                      <Col md={6} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-hospital me-1"></i>
                            Hospital Name *
                          </Form.Label>
                          <Form.Control
                            type="text"
                            name="hospitalName"
                            value={formData.hospitalName}
                            onChange={handleChange}
                            required
                            maxLength={150}
                            placeholder="Enter hospital or clinic name"
                          />
                          <Form.Control.Feedback type="invalid">
                            Please provide the hospital name.
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>

                {/* Blood Request Details */}
                <Card className="mb-4 border-light">
                  <Card.Header className="bg-light">
                    <h6 className="mb-0 text-danger">
                      <i className="fas fa-tint me-2"></i>
                      Blood Request Details
                    </h6>
                  </Card.Header>
                  <Card.Body>
                    <Row>
                      <Col md={4} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-tint me-1"></i>
                            Blood Group Required *
                          </Form.Label>
                          <Form.Select
                            name="bloodGroup"
                            value={formData.bloodGroup}
                            onChange={handleChange}
                            required
                          >
                            <option value="">Select blood group needed</option>
                            {bloodGroups.map(group => (
                              <option key={group} value={group}>{group}</option>
                            ))}
                          </Form.Select>
                          <Form.Control.Feedback type="invalid">
                            Please select the required blood group.
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                      <Col md={4} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-sort-numeric-up me-1"></i>
                            Units Required *
                          </Form.Label>
                          <Form.Control
                            type="number"
                            name="unitsRequested"
                            value={formData.unitsRequested}
                            onChange={handleChange}
                            required
                            min={1}
                            max={10}
                            placeholder="Number of units"
                          />
                          <Form.Control.Feedback type="invalid">
                            Units must be between 1 and 10.
                          </Form.Control.Feedback>
                          <Form.Text className="text-muted">
                            Maximum 10 units per request
                          </Form.Text>
                        </Form.Group>
                      </Col>

                      <Col md={4} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-exclamation-triangle me-1"></i>
                            Urgency Level *
                          </Form.Label>
                          <Form.Select
                            name="urgencyLevel"
                            value={formData.urgencyLevel}
                            onChange={handleChange}
                            required
                          >
                            {urgencyLevels.map(level => (
                              <option key={level.value} value={level.value}>
                                {level.label}
                              </option>
                            ))}
                          </Form.Select>
                        </Form.Group>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>

                {/* Patient Information */}
                <Card className="mb-4 border-light">
                  <Card.Header className="bg-light">
                    <h6 className="mb-0 text-info">
                      <i className="fas fa-user-injured me-2"></i>
                      Patient Information
                    </h6>
                  </Card.Header>
                  <Card.Body>
                    <Row>
                      <Col md={6} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-user-injured me-1"></i>
                            Patient Name *
                          </Form.Label>
                          <Form.Control
                            type="text"
                            name="patientName"
                            value={formData.patientName}
                            onChange={handleChange}
                            required
                            maxLength={100}
                            placeholder="Enter patient's name"
                          />
                          <Form.Control.Feedback type="invalid">
                            Please provide the patient's name.
                          </Form.Control.Feedback>
                        </Form.Group>
                      </Col>

                      <Col md={6} className="mb-3">
                        <Form.Group>
                          <Form.Label>
                            <i className="fas fa-stethoscope me-1"></i>
                            Medical Reason
                          </Form.Label>
                          <Form.Control
                            as="textarea"
                            rows={3}
                            name="medicalReason"
                            value={formData.medicalReason}
                            onChange={handleChange}
                            maxLength={500}
                            placeholder="Brief description of medical condition or reason for blood requirement (optional)"
                          />
                          <Form.Text className="text-muted">
                            Optional: Provide medical reason to help prioritize the request
                          </Form.Text>
                        </Form.Group>
                      </Col>
                    </Row>
                  </Card.Body>
                </Card>

                {/* Urgency Information */}
                <div className="bg-light rounded p-3 mb-4">
                  <h6 className="text-warning mb-2">
                    <i className="fas fa-info-circle me-2"></i>
                    Urgency Level Guidelines
                  </h6>
                  <Row className="small">
                    <Col md={4}>
                      <div className="text-success">
                        <strong>Normal:</strong> Routine medical procedures, elective surgeries
                      </div>
                    </Col>
                    <Col md={4}>
                      <div className="text-warning">
                        <strong>Urgent:</strong> Planned surgeries, medical treatments within 24-48 hours
                      </div>
                    </Col>
                    <Col md={4}>
                      <div className="text-danger">
                        <strong>Emergency:</strong> Life-threatening situations, immediate transfusion needed
                      </div>
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
                        Submitting Request...
                      </>
                    ) : (
                      <>
                        <i className="fas fa-paper-plane me-2"></i>
                        Submit Blood Request
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

export default BloodRequest;