import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Table, Button, Badge, Form, InputGroup, Alert, Spinner } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { bloodRequestService } from '../services/apiService';

/**
 * Request Status page component
 */
const RequestStatus = () => {
  const navigate = useNavigate();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchEmail, setSearchEmail] = useState('');
  const [filterStatus, setFilterStatus] = useState('');

  const statusOptions = [
    { value: 'PENDING', label: 'Pending', color: 'warning' },
    { value: 'APPROVED', label: 'Approved', color: 'success' },
    { value: 'REJECTED', label: 'Rejected', color: 'danger' },
    { value: 'FULFILLED', label: 'Fulfilled', color: 'info' },
    { value: 'CANCELLED', label: 'Cancelled', color: 'secondary' }
  ];

  const urgencyLevels = [
    { value: 'NORMAL', label: 'Normal', color: 'success' },
    { value: 'URGENT', label: 'Urgent', color: 'warning' },
    { value: 'EMERGENCY', label: 'Emergency', color: 'danger' }
  ];

  useEffect(() => {
    fetchRequests();
  }, []);

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const response = await bloodRequestService.getAllRequests();
      setRequests(response.data || []);
      setError(null);
    } catch (error) {
      console.error('Error fetching requests:', error);
      setError('Failed to load blood requests');
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchByEmail = async () => {
    if (!searchEmail.trim()) {
      fetchRequests();
      return;
    }

    try {
      setLoading(true);
      const response = await bloodRequestService.getRequestsByEmail(searchEmail);
      setRequests(response.data || []);
      setError(null);
    } catch (error) {
      console.error('Error searching requests:', error);
      setError('Failed to search requests by email');
    } finally {
      setLoading(false);
    }
  };

  const filteredRequests = requests.filter(request => {
    return filterStatus === '' || request.status === filterStatus;
  });

  const getStatusBadge = (status) => {
    const statusConfig = statusOptions.find(s => s.value === status);
    return (
      <Badge bg={statusConfig?.color || 'secondary'}>
        {statusConfig?.label || status}
      </Badge>
    );
  };

  const getUrgencyBadge = (urgency) => {
    const urgencyConfig = urgencyLevels.find(u => u.value === urgency);
    return (
      <Badge bg={urgencyConfig?.color || 'secondary'} className="me-2">
        {urgencyConfig?.label || urgency}
      </Badge>
    );
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="danger" />
        <p className="mt-3">Loading blood requests...</p>
      </div>
    );
  }

  return (
    <div>
      {/* Page Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold text-danger">
            <i className="fas fa-list-alt me-2"></i>
            Blood Request Status
          </h2>
          <p className="text-muted mb-0">Track and monitor blood request status</p>
        </div>
        <Button 
          variant="danger"
          onClick={() => navigate('/requests/new')}
        >
          <i className="fas fa-plus me-2"></i>
          New Request
        </Button>
      </div>

      {/* Error Alert */}
      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          <i className="fas fa-exclamation-circle me-2"></i>
          {error}
        </Alert>
      )}

      {/* Search and Filter */}
      <Card className="shadow-sm border-0 mb-4">
        <Card.Body>
          <Row>
            <Col md={6}>
              <Form.Group>
                <Form.Label>Search by Email</Form.Label>
                <InputGroup>
                  <Form.Control
                    type="email"
                    placeholder="Enter email to search requests..."
                    value={searchEmail}
                    onChange={(e) => setSearchEmail(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSearchByEmail()}
                  />
                  <Button variant="outline-secondary" onClick={handleSearchByEmail}>
                    <i className="fas fa-search"></i>
                  </Button>
                </InputGroup>
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Filter by Status</Form.Label>
                <Form.Select
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                >
                  <option value="">All Status</option>
                  {statusOptions.map(status => (
                    <option key={status.value} value={status.value}>{status.label}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={2} className="d-flex align-items-end">
              <Button 
                variant="outline-secondary" 
                onClick={() => {
                  setSearchEmail('');
                  setFilterStatus('');
                  fetchRequests();
                }}
              >
                <i className="fas fa-redo me-1"></i>
                Reset
              </Button>
            </Col>
          </Row>
        </Card.Body>
      </Card>

      {/* Statistics */}
      <Row className="g-3 mb-4">
        <Col md={3}>
          <Card className="text-center border-0 bg-warning text-white">
            <Card.Body>
              <h4>{requests.filter(r => r.status === 'PENDING').length}</h4>
              <small>Pending Requests</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-success text-white">
            <Card.Body>
              <h4>{requests.filter(r => r.status === 'APPROVED').length}</h4>
              <small>Approved Requests</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-info text-white">
            <Card.Body>
              <h4>{requests.filter(r => r.status === 'FULFILLED').length}</h4>
              <small>Fulfilled Requests</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-danger text-white">
            <Card.Body>
              <h4>{requests.filter(r => r.urgencyLevel === 'EMERGENCY').length}</h4>
              <small>Emergency Requests</small>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Requests Table */}
      <Card className="shadow-sm border-0">
        <Card.Header className="bg-light">
          <h5 className="mb-0">
            <i className="fas fa-clipboard-list me-2"></i>
            Blood Requests ({filteredRequests.length} found)
          </h5>
        </Card.Header>
        <Card.Body className="p-0">
          {filteredRequests.length === 0 ? (
            <div className="text-center py-5">
              <i className="fas fa-clipboard-list text-muted mb-3" style={{fontSize: '3rem'}}></i>
              <h5 className="text-muted">No requests found</h5>
              <p className="text-muted">
                {requests.length === 0 
                  ? "No blood requests have been submitted yet." 
                  : "Try adjusting your search or filter criteria."
                }
              </p>
              {requests.length === 0 && (
                <Button variant="danger" onClick={() => navigate('/requests/new')}>
                  <i className="fas fa-plus me-2"></i>
                  Submit First Request
                </Button>
              )}
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Request ID</th>
                    <th>Requester</th>
                    <th>Patient</th>
                    <th>Blood Details</th>
                    <th>Urgency</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredRequests.map(request => (
                    <tr key={request.id}>
                      <td>
                        <div className="fw-bold text-primary">#{request.id}</div>
                        <small className="text-muted">{request.hospitalName}</small>
                      </td>
                      <td>
                        <div>
                          <div className="fw-bold">{request.requesterName}</div>
                          <small className="text-muted">{request.contactEmail}</small>
                          <br />
                          <small className="text-muted">{request.contactPhone}</small>
                        </div>
                      </td>
                      <td>
                        <div className="fw-bold">{request.patientName}</div>
                        {request.medicalReason && (
                          <small className="text-muted d-block">
                            {request.medicalReason.length > 50 
                              ? `${request.medicalReason.substring(0, 50)}...`
                              : request.medicalReason
                            }
                          </small>
                        )}
                      </td>
                      <td>
                        <div>
                          <Badge 
                            bg="danger" 
                            className="fs-6 me-2"
                            style={{fontSize: '0.9rem'}}
                          >
                            {request.bloodGroup}
                          </Badge>
                          <span className="fw-bold">{request.unitsRequested}</span>
                          <small className="text-muted"> units</small>
                        </div>
                      </td>
                      <td>
                        {getUrgencyBadge(request.urgencyLevel)}
                      </td>
                      <td>
                        {getStatusBadge(request.status)}
                        {request.processedAt && (
                          <div>
                            <small className="text-muted">
                              Processed: {formatDate(request.processedAt)}
                            </small>
                          </div>
                        )}
                      </td>
                      <td>
                        <div>{formatDate(request.createdAt)}</div>
                      </td>
                      <td>
                        <div className="d-flex gap-1">
                          <Button 
                            size="sm" 
                            variant="outline-primary"
                            title="View Details"
                          >
                            <i className="fas fa-eye"></i>
                          </Button>
                          {request.status === 'PENDING' && (
                            <Button 
                              size="sm" 
                              variant="outline-warning"
                              title="Cancel Request"
                            >
                              <i className="fas fa-times"></i>
                            </Button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>

      {/* Help Section */}
      <Card className="border-0 shadow-sm mt-4">
        <Card.Header className="bg-light">
          <h6 className="mb-0">
            <i className="fas fa-question-circle me-2"></i>
            Request Status Guide
          </h6>
        </Card.Header>
        <Card.Body>
          <Row className="small">
            <Col md={6}>
              <h6 className="text-primary">Status Meanings:</h6>
              <ul className="list-unstyled">
                <li><Badge bg="warning">Pending</Badge> - Request submitted, awaiting review</li>
                <li><Badge bg="success">Approved</Badge> - Request approved, processing</li>
                <li><Badge bg="info">Fulfilled</Badge> - Blood units provided</li>
              </ul>
            </Col>
            <Col md={6}>
              <h6 className="text-primary">What to expect:</h6>
              <ul className="list-unstyled">
                <li><Badge bg="danger">Rejected</Badge> - Request denied (insufficient stock)</li>
                <li><Badge bg="secondary">Cancelled</Badge> - Request cancelled by requester</li>
                <li><strong>Processing Time:</strong> Normal (24-48h), Urgent (12-24h), Emergency (Immediate)</li>
              </ul>
            </Col>
          </Row>
        </Card.Body>
      </Card>
    </div>
  );
};

export default RequestStatus;