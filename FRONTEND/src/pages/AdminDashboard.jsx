import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Table, Button, Badge, Modal, Form, Alert, Spinner, ProgressBar } from 'react-bootstrap';
import { bloodRequestService, bloodInventoryService, donorService, dashboardService } from '../services/apiService';

/**
 * Admin Dashboard component for managing blood bank operations
 */
const AdminDashboard = () => {
  const [stats, setStats] = useState({});
  const [requests, setRequests] = useState([]);
  const [inventory, setInventory] = useState([]);
  const [recentDonors, setRecentDonors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [showRequestModal, setShowRequestModal] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

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
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [statsResponse, requestsResponse, inventoryResponse, donorsResponse] = await Promise.all([
        dashboardService.getDashboardStats(),
        bloodRequestService.getAllRequests(),
        bloodInventoryService.getAllInventory(),
        donorService.getAllDonors()
      ]);

      setStats(statsResponse.data || {});
      setRequests((requestsResponse.data || []).slice(0, 10)); // Latest 10 requests
      setInventory(inventoryResponse.data || []);
      setRecentDonors((donorsResponse.data || []).slice(0, 5)); // Latest 5 donors
      setError(null);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleRequestAction = async (requestId, action) => {
    try {
      setActionLoading(true);
      await bloodRequestService.updateRequestStatus(
        requestId, 
        action, 
        `Request ${action.toLowerCase()} by admin`, 
        'Admin'
      );
      await fetchDashboardData();
      setShowRequestModal(false);
      setSelectedRequest(null);
    } catch (error) {
      console.error('Error updating request:', error);
      setError(`Failed to ${action.toLowerCase()} request`);
    } finally {
      setActionLoading(false);
    }
  };

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
      <Badge bg={urgencyConfig?.color || 'secondary'}>
        {urgencyConfig?.label || urgency}
      </Badge>
    );
  };

  const getStockLevel = (units) => {
    if (units >= 50) return { color: 'success', text: 'Good Stock' };
    if (units >= 20) return { color: 'warning', text: 'Low Stock' };
    return { color: 'danger', text: 'Critical' };
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
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
      {/* Page Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold text-danger">
            <i className="fas fa-tachometer-alt me-2"></i>
            Admin Dashboard
          </h2>
          <p className="text-muted mb-0">Blood Bank Management Overview</p>
        </div>
        <div>
          <Button variant="outline-secondary" onClick={fetchDashboardData} disabled={loading}>
            <i className="fas fa-sync-alt me-2"></i>
            Refresh
          </Button>
        </div>
      </div>

      {/* Error Alert */}
      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          <i className="fas fa-exclamation-circle me-2"></i>
          {error}
        </Alert>
      )}

      {/* Statistics Cards */}
      <Row className="g-3 mb-4">
        <Col md={3}>
          <Card className="text-center border-0 bg-primary text-white h-100">
            <Card.Body>
              <i className="fas fa-users fa-2x mb-2"></i>
              <h3>{stats.totalDonors || 0}</h3>
              <small>Total Donors</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-success text-white h-100">
            <Card.Body>
              <i className="fas fa-tint fa-2x mb-2"></i>
              <h3>{stats.totalUnits || 0}</h3>
              <small>Total Blood Units</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-warning text-white h-100">
            <Card.Body>
              <i className="fas fa-hourglass-half fa-2x mb-2"></i>
              <h3>{stats.pendingRequests || 0}</h3>
              <small>Pending Requests</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-danger text-white h-100">
            <Card.Body>
              <i className="fas fa-exclamation-triangle fa-2x mb-2"></i>
              <h3>{stats.emergencyRequests || 0}</h3>
              <small>Emergency Requests</small>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="g-4">
        {/* Blood Inventory Status */}
        <Col lg={6}>
          <Card className="shadow-sm border-0 h-100">
            <Card.Header className="bg-light">
              <h5 className="mb-0">
                <i className="fas fa-warehouse me-2"></i>
                Blood Inventory Status
              </h5>
            </Card.Header>
            <Card.Body>
              {inventory.length === 0 ? (
                <div className="text-center py-3">
                  <i className="fas fa-box-open text-muted mb-2" style={{fontSize: '2rem'}}></i>
                  <p className="text-muted">No inventory data available</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {inventory.map(item => {
                    const stockLevel = getStockLevel(item.unitsAvailable);
                    const percentage = Math.min((item.unitsAvailable / 100) * 100, 100);
                    
                    return (
                      <div key={item.id} className="border-bottom pb-3 mb-3">
                        <div className="d-flex justify-content-between align-items-center mb-2">
                          <div>
                            <Badge bg="danger" className="me-2">{item.bloodGroup}</Badge>
                            <span className="fw-bold">{item.unitsAvailable} units</span>
                          </div>
                          <Badge bg={stockLevel.color}>{stockLevel.text}</Badge>
                        </div>
                        <ProgressBar 
                          variant={stockLevel.color} 
                          now={percentage} 
                          className="mb-1"
                          style={{height: '8px'}}
                        />
                        <small className="text-muted">
                          Last updated: {formatDate(item.lastUpdated)}
                        </small>
                      </div>
                    );
                  })}
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>

        {/* Recent Donors */}
        <Col lg={6}>
          <Card className="shadow-sm border-0 h-100">
            <Card.Header className="bg-light">
              <h5 className="mb-0">
                <i className="fas fa-user-plus me-2"></i>
                Recent Donors
              </h5>
            </Card.Header>
            <Card.Body>
              {recentDonors.length === 0 ? (
                <div className="text-center py-3">
                  <i className="fas fa-user-friends text-muted mb-2" style={{fontSize: '2rem'}}></i>
                  <p className="text-muted">No recent donors</p>
                </div>
              ) : (
                <div>
                  {recentDonors.map(donor => (
                    <div key={donor.id} className="d-flex justify-content-between align-items-center border-bottom py-2">
                      <div>
                        <div className="fw-bold">{donor.name}</div>
                        <small className="text-muted">{donor.email}</small>
                      </div>
                      <div className="text-end">
                        <Badge bg="danger" className="mb-1">{donor.bloodGroup}</Badge>
                        <div>
                          <small className="text-muted">
                            {formatDate(donor.createdAt)}
                          </small>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Pending Requests */}
      <Card className="shadow-sm border-0 mt-4">
        <Card.Header className="bg-light">
          <h5 className="mb-0">
            <i className="fas fa-clipboard-list me-2"></i>
            Recent Blood Requests
          </h5>
        </Card.Header>
        <Card.Body className="p-0">
          {requests.length === 0 ? (
            <div className="text-center py-4">
              <i className="fas fa-clipboard-list text-muted mb-2" style={{fontSize: '2rem'}}></i>
              <p className="text-muted">No recent requests</p>
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead className="table-light">
                  <tr>
                    <th>ID</th>
                    <th>Hospital</th>
                    <th>Patient</th>
                    <th>Blood</th>
                    <th>Urgency</th>
                    <th>Status</th>
                    <th>Date</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {requests.map(request => (
                    <tr key={request.id}>
                      <td>
                        <span className="fw-bold text-primary">#{request.id}</span>
                      </td>
                      <td>
                        <div>
                          <div className="fw-bold">{request.hospitalName}</div>
                          <small className="text-muted">{request.requesterName}</small>
                        </div>
                      </td>
                      <td>{request.patientName}</td>
                      <td>
                        <Badge bg="danger" className="me-1">{request.bloodGroup}</Badge>
                        <span className="fw-bold">{request.unitsRequested}</span>
                      </td>
                      <td>{getUrgencyBadge(request.urgencyLevel)}</td>
                      <td>{getStatusBadge(request.status)}</td>
                      <td>
                        <small>{formatDate(request.createdAt)}</small>
                      </td>
                      <td>
                        <Button 
                          size="sm" 
                          variant="outline-primary"
                          onClick={() => {
                            setSelectedRequest(request);
                            setShowRequestModal(true);
                          }}
                        >
                          <i className="fas fa-eye me-1"></i>
                          View
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>

      {/* Request Details Modal */}
      <Modal show={showRequestModal} onHide={() => setShowRequestModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            <i className="fas fa-clipboard-list me-2"></i>
            Request Details #{selectedRequest?.id}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedRequest && (
            <Row>
              <Col md={6}>
                <h6 className="text-primary">Requester Information</h6>
                <div className="mb-3">
                  <strong>Name:</strong> {selectedRequest.requesterName}<br/>
                  <strong>Email:</strong> {selectedRequest.contactEmail}<br/>
                  <strong>Phone:</strong> {selectedRequest.contactPhone}<br/>
                  <strong>Hospital:</strong> {selectedRequest.hospitalName}
                </div>

                <h6 className="text-primary">Blood Requirements</h6>
                <div className="mb-3">
                  <strong>Blood Group:</strong> <Badge bg="danger">{selectedRequest.bloodGroup}</Badge><br/>
                  <strong>Units Required:</strong> {selectedRequest.unitsRequested}<br/>
                  <strong>Urgency:</strong> {getUrgencyBadge(selectedRequest.urgencyLevel)}
                </div>
              </Col>
              <Col md={6}>
                <h6 className="text-primary">Patient Information</h6>
                <div className="mb-3">
                  <strong>Patient Name:</strong> {selectedRequest.patientName}<br/>
                  <strong>Medical Reason:</strong> 
                  <div className="mt-1 p-2 bg-light rounded">
                    {selectedRequest.medicalReason || 'Not specified'}
                  </div>
                </div>

                <h6 className="text-primary">Request Status</h6>
                <div className="mb-3">
                  <strong>Current Status:</strong> {getStatusBadge(selectedRequest.status)}<br/>
                  <strong>Submitted:</strong> {formatDate(selectedRequest.createdAt)}
                  {selectedRequest.processedAt && (
                    <>
                      <br/><strong>Processed:</strong> {formatDate(selectedRequest.processedAt)}
                    </>
                  )}
                </div>
              </Col>
            </Row>
          )}
        </Modal.Body>
        <Modal.Footer>
          {selectedRequest?.status === 'PENDING' && (
            <>
              <Button 
                variant="success" 
                onClick={() => handleRequestAction(selectedRequest.id, 'APPROVED')}
                disabled={actionLoading}
              >
                <i className="fas fa-check me-1"></i>
                Approve
              </Button>
              <Button 
                variant="danger" 
                onClick={() => handleRequestAction(selectedRequest.id, 'REJECTED')}
                disabled={actionLoading}
              >
                <i className="fas fa-times me-1"></i>
                Reject
              </Button>
            </>
          )}
          {selectedRequest?.status === 'APPROVED' && (
            <Button 
              variant="info" 
              onClick={() => handleRequestAction(selectedRequest.id, 'FULFILLED')}
              disabled={actionLoading}
            >
              <i className="fas fa-check-double me-1"></i>
              Mark Fulfilled
            </Button>
          )}
          <Button variant="secondary" onClick={() => setShowRequestModal(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default AdminDashboard;