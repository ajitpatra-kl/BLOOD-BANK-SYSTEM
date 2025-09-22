import apiClient from './apiClient';

/**
 * Donor API service functions
 */
export const donorService = {
  // Get all donors
  getAllDonors: () => apiClient.get('/donors'),

  // Get donor by ID
  getDonorById: (id) => apiClient.get(`/donors/${id}`),

  // Get donor by email
  getDonorByEmail: (email) => apiClient.get(`/donors/email/${email}`),

  // Create new donor
  createDonor: (donorData) => apiClient.post('/donors', donorData),

  // Update donor
  updateDonor: (id, donorData) => apiClient.put(`/donors/${id}`, donorData),

  // Delete donor
  deleteDonor: (id) => apiClient.delete(`/donors/${id}`),

  // Get donors by blood group
  getDonorsByBloodGroup: (bloodGroup) => apiClient.get(`/donors/blood-group/${bloodGroup}`),

  // Get eligible donors
  getEligibleDonors: () => apiClient.get('/donors/eligible'),

  // Get eligible donors by blood group
  getEligibleDonorsByBloodGroup: (bloodGroup) => apiClient.get(`/donors/eligible/${bloodGroup}`),

  // Search donors by name
  searchDonorsByName: (name) => apiClient.get(`/donors/search?name=${name}`),

  // Update last donation date
  updateLastDonationDate: (id, date) => apiClient.put(`/donors/${id}/donation-date`, { date }),

  // Get donor statistics
  getDonorStatistics: () => apiClient.get('/donors/statistics'),

  // Get recent donors
  getRecentDonors: () => apiClient.get('/donors/recent'),
};

/**
 * Blood Inventory API service functions
 */
export const bloodInventoryService = {
  // Get all blood inventory
  getAllInventory: () => apiClient.get('/inventory'),

  // Get inventory by ID
  getInventoryById: (id) => apiClient.get(`/inventory/${id}`),

  // Get inventory by blood group
  getInventoryByBloodGroup: (bloodGroup) => apiClient.get(`/inventory/blood-group/${bloodGroup}`),

  // Create new inventory record
  createInventory: (inventoryData) => apiClient.post('/inventory', inventoryData),

  // Update inventory
  updateInventory: (id, inventoryData) => apiClient.put(`/inventory/${id}`, inventoryData),

  // Delete inventory
  deleteInventory: (id) => apiClient.delete(`/inventory/${id}`),

  // Add units to inventory
  addUnits: (id, units, notes) => apiClient.put(`/inventory/${id}/add-units`, { units, notes }),

  // Remove units from inventory
  removeUnits: (id, units, notes) => apiClient.put(`/inventory/${id}/remove-units`, { units, notes }),

  // Get critical shortages
  getCriticalShortages: () => apiClient.get('/inventory/critical'),

  // Get low stock items
  getLowStock: () => apiClient.get('/inventory/low-stock'),

  // Get inventory summary
  getInventorySummary: () => apiClient.get('/inventory/summary'),

  // Get inventory statistics
  getInventoryStatistics: () => apiClient.get('/inventory/statistics'),

  // Check availability for blood group
  checkAvailability: (bloodGroup, units) => apiClient.get(`/inventory/availability/${bloodGroup}/${units}`),
};

/**
 * Blood Request API service functions
 */
export const bloodRequestService = {
  // Get all blood requests
  getAllRequests: () => apiClient.get('/requests'),

  // Get request by ID
  getRequestById: (id) => apiClient.get(`/requests/${id}`),

  // Create new blood request
  createRequest: (requestData) => apiClient.post('/requests', requestData),

  // Update request status
  updateRequestStatus: (id, status, adminNotes, processedBy) => 
    apiClient.put(`/requests/${id}/status`, { status, adminNotes, processedBy }),

  // Delete request
  deleteRequest: (id) => apiClient.delete(`/requests/${id}`),

  // Get requests by status
  getRequestsByStatus: (status) => apiClient.get(`/requests/status/${status}`),

  // Get pending requests
  getPendingRequests: () => apiClient.get('/requests/pending'),

  // Get emergency requests
  getEmergencyRequests: () => apiClient.get('/requests/emergency'),

  // Get requests by blood group
  getRequestsByBloodGroup: (bloodGroup) => apiClient.get(`/requests/blood-group/${bloodGroup}`),

  // Get requests by requester email
  getRequestsByEmail: (email) => apiClient.get(`/requests/email/${email}`),

  // Get request statistics
  getRequestStatistics: () => apiClient.get('/requests/statistics'),

  // Get recent requests
  getRecentRequests: () => apiClient.get('/requests/recent'),

  // Search requests by hospital
  searchByHospital: (hospitalName) => apiClient.get(`/requests/search/hospital?name=${hospitalName}`),

  // Search requests by patient
  searchByPatient: (patientName) => apiClient.get(`/requests/search/patient?name=${patientName}`),
};

/**
 * Dashboard API service functions
 */
export const dashboardService = {
  // Get dashboard statistics
  getDashboardStats: () => apiClient.get('/dashboard/stats'),

  // Get dashboard summary
  getDashboardSummary: () => apiClient.get('/dashboard/summary'),
};

// Export all services as default
export default {
  donor: donorService,
  inventory: bloodInventoryService,
  request: bloodRequestService,
  dashboard: dashboardService,
};