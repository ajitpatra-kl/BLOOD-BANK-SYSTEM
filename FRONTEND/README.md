# Blood Bank Management System - Frontend

A modern React frontend application for the Blood Bank Management System, providing an intuitive interface for managing donors, blood inventory, and requests.

## 🚀 Features

- **Responsive Design**: Mobile-first Bootstrap 5 design that works on all devices
- **Donor Registration**: Easy-to-use form for registering new blood donors
- **Donor Management**: View, search, and manage registered donors
- **Blood Inventory**: Real-time blood stock tracking with visual indicators
- **Request Management**: Submit and track blood requests with urgency levels
- **Admin Dashboard**: Comprehensive overview with statistics and request management
- **Request Status**: Track blood request status with detailed information
- **Dynamic Navigation**: Context-aware navigation with badges and indicators

## 🛠️ Technology Stack

- **Framework**: React 18.2.0
- **Build Tool**: Vite 4.4.5
- **UI Framework**: Bootstrap 5.3.0
- **Routing**: React Router 6.14.2
- **HTTP Client**: Axios 1.4.0
- **Icons**: Font Awesome 6.4.0
- **Styling**: CSS3 with custom Bootstrap themes

## 📋 Prerequisites

- Node.js 16.0+ 
- npm 8.0+ or yarn 1.22+
- Modern web browser (Chrome, Firefox, Safari, Edge)

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd blood-bank-system/bloodbank-frontend
```

### 2. Install Dependencies

```bash
npm install
# or
yarn install
```

### 3. Environment Configuration

Create `.env` file in the root directory:

```env
# Backend API Configuration
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_NAME=Blood Bank Management System
VITE_APP_VERSION=1.0.0

# Development Configuration
VITE_DEV_SERVER_PORT=3000
VITE_DEV_SERVER_HOST=localhost
```

### 4. Start Development Server

```bash
npm run dev
# or
yarn dev
```

The application will start on `http://localhost:3000`

## 🏗️ Build for Production

```bash
npm run build
# or
yarn build
```

This creates an optimized production build in the `dist/` directory.

## 📁 Project Structure

```
src/
├── components/          # Reusable components
│   ├── Navigation.jsx   # Main navigation bar
│   └── Footer.jsx       # Application footer
├── pages/              # Page components
│   ├── Home.jsx        # Dashboard/Landing page
│   ├── DonorRegistration.jsx  # Donor registration form
│   ├── DonorList.jsx   # Donor management page
│   ├── BloodInventory.jsx     # Blood inventory management
│   ├── BloodRequest.jsx       # Blood request form
│   ├── AdminDashboard.jsx     # Admin dashboard
│   └── RequestStatus.jsx      # Request tracking page
├── services/           # API services
│   ├── apiClient.js    # Axios configuration
│   └── apiService.js   # API service methods
├── assets/            # Static assets
│   └── react.svg      # Application assets
├── App.jsx           # Main application component
├── App.css          # Application styles
├── index.css        # Global styles
└── main.jsx        # Application entry point
```

## 🎨 Components Overview

### Navigation Component
- Responsive Bootstrap navbar
- Active route highlighting
- Emergency contact information
- Request count badges

### Home/Dashboard
- Welcome hero section
- Key statistics cards
- Quick action buttons
- Recent activity overview

### Donor Registration
- Multi-step form with validation
- Blood group selection
- Eligibility criteria display
- Real-time form validation

### Donor List
- Searchable donor table
- Filter by blood group and eligibility
- CRUD operations with modals
- Export functionality

### Blood Inventory
- Visual stock level indicators
- Add/Remove units functionality
- Critical stock alerts
- Blood group management

### Blood Request
- Comprehensive request form
- Urgency level selection
- Patient information capture
- Medical reason documentation

### Admin Dashboard
- System statistics overview
- Recent requests management
- Request approval workflow
- Inventory status monitoring

### Request Status
- Request tracking by email
- Status filtering and search
- Request history view
- Status change notifications

## 🔧 Configuration

### API Configuration

The application uses Axios for API communication. Configure the base URL in `src/services/apiClient.js`:

```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
```

### Styling Configuration

Custom styles are defined in `src/App.css` with Bootstrap overrides:

- Brand colors: Primary (#dc3545), Success (#28a745)
- Custom components: Cards, buttons, forms
- Responsive breakpoints
- Animation effects

## 🌐 API Integration

### Service Structure

```javascript
// Example API service usage
import { donorService, bloodInventoryService, bloodRequestService } from './services/apiService';

// Fetch all donors
const donors = await donorService.getAllDonors();

// Create blood request
const request = await bloodRequestService.createRequest(requestData);

// Update inventory
const inventory = await bloodInventoryService.updateUnits(id, units);
```

### Error Handling

The application includes comprehensive error handling:

- Global error boundaries
- API error interceptors
- User-friendly error messages
- Retry mechanisms

## 📱 Responsive Design

### Breakpoints
- **Mobile**: < 768px
- **Tablet**: 768px - 992px
- **Desktop**: > 992px

### Mobile Optimizations
- Touch-friendly buttons
- Collapsible navigation
- Optimized form layouts
- Swipe gestures support

## 🧪 Testing

### Unit Tests
```bash
npm run test
# or
yarn test
```

### End-to-End Tests
```bash
npm run test:e2e
# or
yarn test:e2e
```

## 🚀 Deployment

### Netlify Deployment

1. Build the project:
```bash
npm run build
```

2. Deploy the `dist/` folder to Netlify

3. Configure environment variables in Netlify dashboard

### Vercel Deployment

1. Install Vercel CLI:
```bash
npm i -g vercel
```

2. Deploy:
```bash
vercel --prod
```

### Manual Deployment

1. Build the project
2. Upload `dist/` contents to your web server
3. Configure web server for SPA routing

## 🔒 Security Considerations

- Input validation on all forms
- XSS prevention with React's built-in protections
- CSRF protection via API tokens
- Secure HTTP headers configuration
- Content Security Policy (CSP) implementation

## 🎯 Performance Optimizations

- Code splitting with React.lazy()
- Image optimization
- Bundle size optimization
- Caching strategies
- Service worker implementation

## 🛠️ Development Guidelines

### Code Style
- ESLint configuration for consistent code style
- Prettier for code formatting
- Component-based architecture
- Props validation with PropTypes

### Git Workflow
1. Create feature branches from `main`
2. Follow conventional commit messages
3. Submit pull requests for code review
4. Ensure all tests pass before merging

## 📊 Browser Support

- Chrome (latest 2 versions)
- Firefox (latest 2 versions)
- Safari (latest 2 versions)
- Edge (latest 2 versions)
- Mobile browsers (iOS Safari, Chrome Mobile)

## 🔧 Troubleshooting

### Common Issues

1. **CORS Errors**: Ensure backend CORS is configured for frontend URL
2. **API Connection**: Verify backend is running and accessible
3. **Build Errors**: Clear `node_modules` and reinstall dependencies
4. **Port Conflicts**: Change port in `vite.config.js`

### Development Tips

- Use React Developer Tools for debugging
- Enable source maps in development
- Monitor network requests in browser DevTools
- Use console logging for state debugging

## 📚 Additional Resources

- [React Documentation](https://reactjs.org/docs)
- [Bootstrap Documentation](https://getbootstrap.com/docs)
- [React Router Documentation](https://reactrouter.com/docs)
- [Axios Documentation](https://axios-http.com/docs)
- [Vite Documentation](https://vitejs.dev/guide)

## 📝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Made with ❤️ for blood donation and healthcare management**+ Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
