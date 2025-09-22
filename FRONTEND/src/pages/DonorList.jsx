import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Table, Button, Badge, Form, InputGroup, Alert, Spinner, Modal } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { donorService } from '../services/apiService';

/**
 * Donor List page component
 */
const DonorList = () => {
  const navigate = useNavigate();
  const [donors, setDonors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterBloodGroup, setFilterBloodGroup] = useState('');
  const [selectedDonor, setSelectedDonor] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

  useEffect(() => {
    fetchDonors();
  }, []);

  const fetchDonors = async () => {
    try {
      setLoading(true);
      const response = await donorService.getAllDonors();
      setDonors(response.data || []);
      setError(null);
    } catch (error) {
      console.error('Error fetching donors:', error);
      setError('Failed to load donors');
      setDonors([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!selectedDonor) return;
    
    try {
      await donorService.deleteDonor(selectedDonor.id);
      setDonors(donors.filter(donor => donor.id !== selectedDonor.id));
      setShowDeleteModal(false);
      setSelectedDonor(null);
    } catch (error) {
      console.error('Error deleting donor:', error);
      setError('Failed to delete donor');
    }
  };

  const filteredDonors = donors.filter(donor => {
    const matchesSearch = donor.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         donor.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         donor.phone.includes(searchTerm);
    const matchesBloodGroup = filterBloodGroup === '' || donor.bloodGroup === filterBloodGroup;
    return matchesSearch && matchesBloodGroup;
  });

  const getEligibilityBadge = (donor) => {
    if (!donor.isEligible) {
      return <Badge bg="danger">Not Eligible</Badge>;
    }
    if (donor.canDonate) {
      return <Badge bg="success">Available</Badge>;
    }
    return <Badge bg="warning">Waiting Period</Badge>;
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleDateString();
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="danger" />
        <p className="mt-3">Loading donors...</p>
      </div>
    );
  }

  return (
    <div>
      {/* Page Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold text-danger">
            <i className="fas fa-users me-2"></i>
            Registered Donors
          </h2>
          <p className="text-muted mb-0">Manage and view all registered blood donors</p>
        </div>
        <Button 
          variant="danger"
          onClick={() => navigate('/donors/register')}
        >
          <i className="fas fa-user-plus me-2"></i>
          Add New Donor
        </Button>
      </div>

      {/* Error Alert */}
      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          <i className="fas fa-exclamation-circle me-2"></i>
          {error}
        </Alert>
      )}

      {/* Filters */}
      <Card className="shadow-sm border-0 mb-4">
        <Card.Body>
          <Row>
            <Col md={6}>
              <Form.Group>
                <Form.Label>Search Donors</Form.Label>
                <InputGroup>
                  <InputGroup.Text>
                    <i className="fas fa-search"></i>
                  </InputGroup.Text>
                  <Form.Control
                    type="text"
                    placeholder="Search by name, email, or phone..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                </InputGroup>
              </Form.Group>
            </Col>
            <Col md={4}>
              <Form.Group>
                <Form.Label>Filter by Blood Group</Form.Label>
                <Form.Select
                  value={filterBloodGroup}
                  onChange={(e) => setFilterBloodGroup(e.target.value)}
                >
                  <option value="">All Blood Groups</option>
                  {bloodGroups.map(group => (
                    <option key={group} value={group}>{group}</option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={2} className="d-flex align-items-end">
              <Button 
                variant="outline-secondary" 
                onClick={() => {
                  setSearchTerm('');
                  setFilterBloodGroup('');
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
          <Card className="text-center border-0 bg-primary text-white">
            <Card.Body>
              <h4>{donors.length}</h4>
              <small>Total Donors</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-success text-white">
            <Card.Body>
              <h4>{donors.filter(d => d.canDonate).length}</h4>
              <small>Available Donors</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-warning text-white">
            <Card.Body>
              <h4>{donors.filter(d => d.isEligible && !d.canDonate).length}</h4>
              <small>In Waiting Period</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-danger text-white">
            <Card.Body>
              <h4>{donors.filter(d => !d.isEligible).length}</h4>
              <small>Not Eligible</small>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Donors Table */}
      <Card className="shadow-sm border-0">
        <Card.Header className="bg-light">
          <h5 className="mb-0">
            <i className="fas fa-list me-2"></i>
            Donors List ({filteredDonors.length} found)
          </h5>
        </Card.Header>
        <Card.Body className="p-0">
          {filteredDonors.length === 0 ? (
            <div className="text-center py-5">
              <i className="fas fa-users text-muted mb-3" style={{fontSize: '3rem'}}></i>
              <h5 className="text-muted">No donors found</h5>
              <p className="text-muted">
                {donors.length === 0 
                  ? "No donors have been registered yet." 
                  : "Try adjusting your search or filter criteria."
                }
              </p>
              {donors.length === 0 && (
                <Button variant="danger" onClick={() => navigate('/donors/register')}>
                  <i className="fas fa-user-plus me-2"></i>
                  Register First Donor
                </Button>
              )}
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Name</th>
                    <th>Contact</th>
                    <th>Blood Group</th>
                    <th>Age</th>
                    <th>Last Donation</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredDonors.map(donor => (
                    <tr key={donor.id}>
                      <td>
                        <div>
                          <div className="fw-bold">{donor.name}</div>
                          <small className="text-muted">{donor.email}</small>
                        </div>
                      </td>
                      <td>
                        <div>
                          <div>{donor.phone}</div>
                          <small className="text-muted">
                            <i className="fas fa-map-marker-alt me-1"></i>
                            ID: {donor.id}
                          </small>
                        </div>
                      </td>
                      <td>
                        <Badge 
                          bg="danger" 
                          className="fs-6"
                          style={{fontSize: '0.9rem'}}
                        >
                          {donor.bloodGroup}
                        </Badge>
                      </td>
                      <td>
                        <span>{donor.age} years</span>
                        <br />
                        <small className="text-muted">{donor.weight} kg</small>
                      </td>
                      <td>
                        <div>{formatDate(donor.lastDonationDate)}</div>
                      </td>
                      <td>
                        {getEligibilityBadge(donor)}
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
                          <Button 
                            size="sm" 
                            variant="outline-warning"
                            title="Edit Donor"
                          >
                            <i className="fas fa-edit"></i>
                          </Button>
                          <Button 
                            size="sm" 
                            variant="outline-danger"
                            title="Delete Donor"
                            onClick={() => {
                              setSelectedDonor(donor);
                              setShowDeleteModal(true);
                            }}
                          >
                            <i className="fas fa-trash"></i>
                          </Button>
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

      {/* Delete Confirmation Modal */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            <i className="fas fa-exclamation-triangle text-warning me-2"></i>
            Confirm Delete
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Are you sure you want to delete donor <strong>{selectedDonor?.name}</strong>?
          <br />
          <small className="text-muted">This action cannot be undone.</small>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Cancel
          </Button>
          <Button variant="danger" onClick={handleDelete}>
            <i className="fas fa-trash me-2"></i>
            Delete Donor
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default DonorList;