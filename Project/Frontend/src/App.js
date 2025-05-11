// Import necessary modules and components
import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, useLocation } from "react-router-dom";
import Dashboard from "./Dashboard";
import LoginPage from "./LoginPage";
import "./styles.css"; // Import custom CSS styles

// Main App component
const App = () => {
  // Initialize login state from localStorage (persists login across refresh)
  const [isLoggedIn, setIsLoggedIn] = useState(() => {
    return localStorage.getItem("isLoggedIn") === "true"; });

  // Get the current route/path
  const location = useLocation(); 

  return (
    <div className="container">
      {/* Main Header with Logo and Logout functionality */}
      <header className="header">
        <div className="header-content">
          <img src="/images/Headerlogo.png" alt="Commerce Bank Logo" className="logo" />
        </div>

        {/* Display 'Logged in as' and Logout button only if logged in and on dashboard page */}
        {isLoggedIn && location.pathname === "/dashboard" && (
          <div className="header-user-info">
            <span>Logged in as: {localStorage.getItem("userId")}</span>
            <button
              className="logout-btn"
              onClick={() => {
                localStorage.clear(); // Clear user data
                setIsLoggedIn(false); // Update login state
                window.location.href = "/login"; // Redirect to login
              }}
            >
              Logout
            </button>
          </div>
        )}
      </header>

      {/* FDIC Sub-header below the main header */}
      <div className="sub-header">
        <span className="fdic-logo">FDIC</span>
        <span className="fdic-text">
          FDIC-Insured – Backed by the full faith and credit of the U.S. Government
        </span>
      </div>

      {/* Routing for login and dashboard pages */}
      <Routes>
        <Route path="/" element={<LoginPage setIsLoggedIn={setIsLoggedIn} />} />
        <Route path="/dashboard" element={<Dashboard setIsLoggedIn={setIsLoggedIn} />} />
        <Route path="/login" element={<LoginPage setIsLoggedIn={setIsLoggedIn} />} />
      </Routes>
    </div>
  );
};

// AppWrapper needed because useLocation must be inside a Router
const AppWrapper = () => (
  <Router>
    <App />
  </Router>
);

export default AppWrapper;
