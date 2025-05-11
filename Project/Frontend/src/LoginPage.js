// Import necessary modules
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./styles.css"; // Import custom CSS styles

// LoginPage handles user authentication UI and logic
const LoginPage = ({ setIsLoggedIn }) => {
    const [username, setUsername] = useState(""); // Track entered username
    const [password, setPassword] = useState(""); // Track entered password
    const navigate = useNavigate(); // Navigation hook for redirecting

    // Handle login form submission
    const handleLogin = () => {
        console.log("Logging in:", username);

        const loginRequest = {
            userId: username,
            password: password
        };

        // Send login request to backend
        fetch("http://localhost:8081/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(loginRequest),
        })
            .then((response) => {
                if (response.ok) {
                    // If login successful, store credentials and redirect
                    localStorage.setItem("isLoggedIn", "true");
                    localStorage.setItem("userId", username);
                    setIsLoggedIn(true);
                    navigate("/dashboard");
                } else {
                    alert("Invalid login credentials.");
                }
            })
            .catch((error) => console.error("Login error:", error));
    };

    return (
        <div className="login-container">
            <div className="login-box">
                <h2>Login</h2>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <button className="login-btn" onClick={handleLogin}>Login</button>
            </div>
        </div>
    );
};

export default LoginPage;
