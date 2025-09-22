import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Container } from 'react-bootstrap';

// Import components
import Navigation from './components/Navigation';
import Footer from './components/Footer';

// Import pages
import Home from './pages/Home';
import DonorRegistration from './pages/DonorRegistration';
import DonorList from './pages/DonorList';
import BloodInventory from './pages/BloodInventory';
import BloodRequest from './pages/BloodRequest';
import AdminDashboard from './pages/AdminDashboard';
import RequestStatus from './pages/RequestStatus';

// Import Bootstrap CSS
import 'bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/css/all.min.css';
import './App.css';

/**
 * Main App component with routing configuration
 */
function App() {
  return (
    <Router>
      <div className="App d-flex flex-column min-vh-100">
        {/* Navigation */}
        <Navigation />
        
        {/* Main Content */}
        <main className="flex-grow-1">
          <Container fluid className="py-4">
            <Routes>
              {/* Home/Dashboard */}
              <Route path="/" element={<Home />} />
              <Route path="/home" element={<Home />} />
              
              {/* Donor Routes */}
              <Route path="/donors/register" element={<DonorRegistration />} />
              <Route path="/donors/list" element={<DonorList />} />
              
              {/* Blood Inventory Routes */}
              <Route path="/inventory" element={<BloodInventory />} />
              
              {/* Blood Request Routes */}
              <Route path="/requests/new" element={<BloodRequest />} />
              <Route path="/requests/status" element={<RequestStatus />} />
              
              {/* Admin Routes */}
              <Route path="/admin" element={<AdminDashboard />} />
              
              {/* Fallback route */}
              <Route path="*" element={<Home />} />
            </Routes>
          </Container>
        </main>
        
        {/* Footer */}
        <Footer />
      </div>
    </Router>
  );
}

export default App;
