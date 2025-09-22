import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Table, Button, Badge, Form, Modal, Alert, Spinner } from 'react-bootstrap';
import { bloodInventoryService } from '../services/apiService';

/**
 * Blood Inventory page component
 */
const BloodInventory = () => {
  const [inventory, setInventory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [selectedInventory, setSelectedInventory] = useState(null);
  const [updateType, setUpdateType] = useState('add'); // 'add' or 'remove'

  const [newInventory, setNewInventory] = useState({
    bloodGroup: '',
    unitsAvailable: 0,
    minimumStock: 5,
    maximumCapacity: 100,
    notes: ''
  });

  const [updateForm, setUpdateForm] = useState({
    units: 0,
    notes: ''
  });

  const bloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

  useEffect(() => {
    fetchInventory();
  }, []);

  const fetchInventory = async () => {
    try {
      setLoading(true);
      const response = await bloodInventoryService.getAllInventory();
      setInventory(response.data || []);
      setError(null);
    } catch (error) {
      console.error('Error fetching inventory:', error);
      setError('Failed to load blood inventory');
      setInventory([]);
    } finally {
      setLoading(false);
    }
  };

  const handleAddInventory = async (e) => {
    e.preventDefault();
    try {
      const response = await bloodInventoryService.createInventory(newInventory);
      if (response.success) {
        setInventory([...inventory, response.data]);
        setShowAddModal(false);
        setNewInventory({
          bloodGroup: '',
          unitsAvailable: 0,
          minimumStock: 5,
          maximumCapacity: 100,
          notes: ''
        });
      }
    } catch (error) {
      console.error('Error adding inventory:', error);
      setError('Failed to add blood group to inventory');
    }
  };

  const handleUpdateUnits = async (e) => {
    e.preventDefault();
    if (!selectedInventory) return;

    try {
      const { units, notes } = updateForm;
      let response;
      
      if (updateType === 'add') {
        response = await bloodInventoryService.addUnits(selectedInventory.id, units, notes);
      } else {
        response = await bloodInventoryService.removeUnits(selectedInventory.id, units, notes);
      }

      if (response.success) {
        // Update local state
        setInventory(inventory.map(item => 
          item.id === selectedInventory.id ? response.data : item
        ));
        setShowUpdateModal(false);
        setUpdateForm({ units: 0, notes: '' });
        setSelectedInventory(null);
      }
    } catch (error) {
      console.error('Error updating units:', error);
      setError(`Failed to ${updateType} units`);
    }
  };

  const getStatusBadge = (item) => {
    const status = item.stockStatus || 'ADEQUATE';
    switch (status) {
      case 'OUT_OF_STOCK':
        return <Badge bg="dark">Out of Stock</Badge>;
      case 'CRITICAL':
        return <Badge bg="danger">Critical</Badge>;
      case 'LOW':
        return <Badge bg="warning">Low Stock</Badge>;
      case 'ADEQUATE':
        return <Badge bg="success">Adequate</Badge>;
      default:
        return <Badge bg="secondary">Unknown</Badge>;
    }
  };

  const getProgressVariant = (item) => {
    const status = item.stockStatus || 'ADEQUATE';
    switch (status) {
      case 'OUT_OF_STOCK':
        return 'dark';
      case 'CRITICAL':
        return 'danger';
      case 'LOW':
        return 'warning';
      default:
        return 'success';
    }
  };

  const getProgressPercentage = (item) => {
    return Math.min((item.unitsAvailable / item.maximumCapacity) * 100, 100);
  };

  if (loading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="danger" />
        <p className="mt-3">Loading blood inventory...</p>
      </div>
    );
  }

  return (
    <div>
      {/* Page Header */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold text-danger">
            <i className="fas fa-flask me-2"></i>
            Blood Inventory
          </h2>
          <p className="text-muted mb-0">Manage blood units availability and stock levels</p>
        </div>
        <Button 
          variant="danger"
          onClick={() => setShowAddModal(true)}
        >
          <i className="fas fa-plus me-2"></i>
          Add Blood Group
        </Button>
      </div>

      {/* Error Alert */}
      {error && (
        <Alert variant="danger" dismissible onClose={() => setError(null)}>
          <i className="fas fa-exclamation-circle me-2"></i>
          {error}
        </Alert>
      )}

      {/* Inventory Summary */}
      <Row className="g-3 mb-4">
        <Col md={3}>
          <Card className="text-center border-0 bg-primary text-white">
            <Card.Body>
              <h4>{inventory.reduce((sum, item) => sum + item.unitsAvailable, 0)}</h4>
              <small>Total Units</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-success text-white">
            <Card.Body>
              <h4>{inventory.filter(item => item.stockStatus === 'ADEQUATE').length}</h4>
              <small>Adequate Stock</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-warning text-white">
            <Card.Body>
              <h4>{inventory.filter(item => ['LOW', 'CRITICAL'].includes(item.stockStatus)).length}</h4>
              <small>Low/Critical</small>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="text-center border-0 bg-danger text-white">
            <Card.Body>
              <h4>{inventory.filter(item => item.stockStatus === 'OUT_OF_STOCK').length}</h4>
              <small>Out of Stock</small>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Inventory Table */}
      <Card className="shadow-sm border-0">
        <Card.Header className="bg-light">
          <h5 className="mb-0">
            <i className="fas fa-list me-2"></i>
            Blood Inventory Status
          </h5>
        </Card.Header>
        <Card.Body className="p-0">
          {inventory.length === 0 ? (
            <div className="text-center py-5">
              <i className="fas fa-flask text-muted mb-3" style={{fontSize: '3rem'}}></i>
              <h5 className="text-muted">No inventory records found</h5>
              <p className="text-muted">Start by adding blood groups to the inventory.</p>
              <Button variant="danger" onClick={() => setShowAddModal(true)}>
                <i className="fas fa-plus me-2"></i>
                Add First Blood Group
              </Button>
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover className="mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Blood Group</th>
                    <th>Available Units</th>
                    <th>Stock Level</th>
                    <th>Status</th>
                    <th>Capacity</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {inventory.map(item => (
                    <tr key={item.id}>
                      <td>
                        <Badge 
                          bg="danger" 
                          className="fs-5"
                          style={{fontSize: '1.1rem'}}
                        >
                          {item.bloodGroup}
                        </Badge>
                      </td>
                      <td>
                        <div className="fw-bold fs-4">{item.unitsAvailable}</div>
                        <small className="text-muted">units</small>
                      </td>
                      <td>
                        <div className="mb-1">
                          <div 
                            className="progress" 
                            style={{height: '8px'}}
                          >
                            <div 
                              className={`progress-bar bg-${getProgressVariant(item)}`}
                              style={{width: `${getProgressPercentage(item)}%`}}
                            ></div>
                          </div>
                        </div>
                        <small className="text-muted">
                          Min: {item.minimumStock} | Max: {item.maximumCapacity}
                        </small>
                      </td>
                      <td>
                        {getStatusBadge(item)}
                      </td>
                      <td>
                        <div>
                          <div className="fw-bold">{item.maximumCapacity}</div>
                          <small className="text-muted">
                            {((item.unitsAvailable / item.maximumCapacity) * 100).toFixed(1)}% full
                          </small>
                        </div>
                      </td>
                      <td>
                        <div className="d-flex gap-1">
                          <Button 
                            size="sm" 
                            variant="outline-success"
                            title="Add Units"
                            onClick={() => {
                              setSelectedInventory(item);
                              setUpdateType('add');
                              setShowUpdateModal(true);
                            }}
                          >
                            <i className="fas fa-plus"></i>
                          </Button>
                          <Button 
                            size="sm" 
                            variant="outline-warning"
                            title="Remove Units"
                            onClick={() => {
                              setSelectedInventory(item);
                              setUpdateType('remove');
                              setShowUpdateModal(true);
                            }}
                          >
                            <i className="fas fa-minus"></i>
                          </Button>
                          <Button 
                            size="sm" 
                            variant="outline-primary"
                            title="Edit Settings"
                          >
                            <i className="fas fa-cog"></i>
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

      {/* Add Blood Group Modal */}
      <Modal show={showAddModal} onHide={() => setShowAddModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            <i className="fas fa-plus me-2"></i>
            Add Blood Group to Inventory
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleAddInventory}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Blood Group *</Form.Label>
                  <Form.Select
                    value={newInventory.bloodGroup}
                    onChange={(e) => setNewInventory({...newInventory, bloodGroup: e.target.value})}
                    required
                  >
                    <option value="">Select Blood Group</option>
                    {bloodGroups.filter(group => 
                      !inventory.some(item => item.bloodGroup === group)
                    ).map(group => (
                      <option key={group} value={group}>{group}</option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Initial Units</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    value={newInventory.unitsAvailable}
                    onChange={(e) => setNewInventory({...newInventory, unitsAvailable: parseInt(e.target.value)})}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Minimum Stock Level</Form.Label>
                  <Form.Control
                    type="number"
                    min="0"
                    value={newInventory.minimumStock}
                    onChange={(e) => setNewInventory({...newInventory, minimumStock: parseInt(e.target.value)})}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Maximum Capacity</Form.Label>
                  <Form.Control
                    type="number"
                    min="1"
                    value={newInventory.maximumCapacity}
                    onChange={(e) => setNewInventory({...newInventory, maximumCapacity: parseInt(e.target.value)})}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Notes</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={newInventory.notes}
                onChange={(e) => setNewInventory({...newInventory, notes: e.target.value})}
                placeholder="Optional notes about this blood group inventory..."
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowAddModal(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="danger">
              <i className="fas fa-save me-2"></i>
              Add to Inventory
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Update Units Modal */}
      <Modal show={showUpdateModal} onHide={() => setShowUpdateModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>
            <i className={`fas fa-${updateType === 'add' ? 'plus' : 'minus'} me-2`}></i>
            {updateType === 'add' ? 'Add' : 'Remove'} Blood Units
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleUpdateUnits}>
          <Modal.Body>
            <div className="mb-3">
              <strong>Blood Group:</strong> {selectedInventory?.bloodGroup}
              <br />
              <strong>Current Units:</strong> {selectedInventory?.unitsAvailable}
            </div>
            <Form.Group className="mb-3">
              <Form.Label>Number of Units to {updateType}</Form.Label>
              <Form.Control
                type="number"
                min="1"
                max={updateType === 'remove' ? selectedInventory?.unitsAvailable : 50}
                value={updateForm.units}
                onChange={(e) => setUpdateForm({...updateForm, units: parseInt(e.target.value)})}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Notes</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={updateForm.notes}
                onChange={(e) => setUpdateForm({...updateForm, notes: e.target.value})}
                placeholder={`Reason for ${updateType === 'add' ? 'adding' : 'removing'} units...`}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowUpdateModal(false)}>
              Cancel
            </Button>
            <Button 
              type="submit" 
              variant={updateType === 'add' ? 'success' : 'warning'}
            >
              <i className={`fas fa-${updateType === 'add' ? 'plus' : 'minus'} me-2`}></i>
              {updateType === 'add' ? 'Add' : 'Remove'} Units
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};

export default BloodInventory;