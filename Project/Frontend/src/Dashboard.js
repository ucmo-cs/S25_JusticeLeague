// Import necessary modules
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./styles.css"; // Import custom CSS styles

// Dashboard component to manage URLs
const Dashboard = ({ setIsLoggedIn }) => {
  const navigate = useNavigate();
  const [savedUrls, setSavedUrls] = useState([]); // URLs saved for the user
  const [newUrl, setNewUrl] = useState(""); // New URL input value

  // Fetch user's URLs when the component mounts
  useEffect(() => {
    const userId = localStorage.getItem("userId");
    fetch(`http://localhost:8081/url/${userId}`)
      .then((response) => response.json())
      .then((data) => {
        console.log("Fetched URLs:", data);
        setSavedUrls(data);
      })
      .catch((error) => console.error("Error fetching URLs:", error));
  }, []);

  // Logout handler
  const handleLogout = () => {
    localStorage.setItem("isLoggedIn", "false");
    setIsLoggedIn(false);
    navigate("/login");
  };

  // Format and display important headers
  const formatHeadersForDisplay = (headers) => {
    if (!headers) return "No headers available.";
  
    const importantKeys = [
      "Server",
      "Date",
      "Content-Type",
      "Cache-Control",
      "X-Frame-Options",
      "Strict-Transport-Security",
      "Content-Security-Policy",
      "Expires"
    ];
  
    const headerLines = headers.split(/\r?\n/);

    // Filter only important headers
    const importantHeaders = headerLines
      .map(line => {
        const [key, ...rest] = line.split(": ");
        return { key: key.trim(), value: rest.join(": ").trim() };
      })
      .filter(header => importantKeys.includes(header.key));
  
    if (importantHeaders.length === 0) {
      return "No important headers found.";
    }
  
    return importantHeaders
      .map(header => `<strong>${header.key}:</strong> ${header.value}`)
      .join("<br/>");
  };

  // Handle checkbox selection for each URL
  const handleCheckboxChange = (id) => {
    setSavedUrls((prevUrls) =>
      prevUrls.map((url) =>
        url.url_id === id ? { ...url, selected: !url.selected } : url
      )
    );
  };

  // Delete selected URLs
  const handleDelete = () => {
    const selectedIds = savedUrls.filter(url => url.selected).map(url => url.url_id);

    if (selectedIds.length === 0) {
        alert("Please select at least one URL to delete.");
        return;
    }

    const userId = localStorage.getItem("userId");

    fetch("http://localhost:8081/url", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ urlIds: selectedIds, userId }), 
    })
    .then((response) => response.json())
    .then((updatedUrls) => {
        setSavedUrls(updatedUrls); // Update UI after delete
        alert("Selected URLs deleted.");
    })
    .catch((error) => {
        console.error("Error deleting URLs:", error);
    });
  }; 

  // Add a new URL
  const handleAddUrl = () => {
    if (newUrl.trim() === "") {
      alert("Please enter a URL.");
      return;
    }
  
    let validUrl;
    try {
      validUrl = new URL(newUrl);
    } catch (_) {
      alert("Invalid URL. Please retype a valid URL (example: https://example.com)");
      return;
    }
  
    if (!["http:", "https:"].includes(validUrl.protocol)) {
      alert("Only HTTP and HTTPS URLs are allowed.");
      return;
    }
  
    const userId = localStorage.getItem("userId");
  
    const urlRequest = {
      url: validUrl.href,  
      userId: userId,
    };
  
    fetch("http://localhost:8081/url", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(urlRequest),
    })
    .then((res) => res.json())
    .then((newSavedUrl) => {
      setSavedUrls((prevUrls) => [...prevUrls, newSavedUrl]);
      setNewUrl(""); // Clear input
    })
    .catch((error) => {
      console.error("Failed to add URL:", error);
    });
  };

  // Edit selected URLs
  const handleEditSelected = () => {
    const updatedUrls = savedUrls.filter(url => url.selected).map(url => {
      const updatedUrl = document.getElementById(`url-${url.url_id}`).innerText;
      return {
        ...url,
        url: updatedUrl,
      };
    });

    if (updatedUrls.length === 0) {
      alert("Please select at least one URL to edit.");
      return;
    }

    fetch("http://localhost:8081/urls", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updatedUrls),
    })
    .then((response) => response.json())
    .then((responseData) => {
      console.log("Updated URLs:", responseData);
      setSavedUrls((prevUrls) =>
        prevUrls.map((url) => {
          const updatedUrl = responseData.find((u) => u.url_id === url.url_id);
          return updatedUrl ? updatedUrl : url;
        })
      );
    })
    .catch((error) => {
      console.error("Error updating URLs:", error);
    });
  };

  // Rescan selected URLs
  const handleRescan = () => {
    const selectedUrls = savedUrls.filter(url => url.selected);

    if (selectedUrls.length === 0) {
      alert("Please select at least one URL to rescan.");
      return;
    }

    const selectedUrlIds = selectedUrls.map(url => url.url_id);

    fetch("http://localhost:8081/rescan", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectedUrlIds),
    })
    .then((response) => response.json())
    .then((updatedUrls) => {
      console.log("Updated URLs:", updatedUrls);
      if (Array.isArray(updatedUrls)) {
        setSavedUrls((prevUrls) =>
          prevUrls.map((url) => {
            const updatedUrl = updatedUrls.find((u) => u.url_id === url.url_id);
            return updatedUrl ? updatedUrl : url;
          })
        );
      } else {
        console.error("Unexpected response format:", updatedUrls);
      }
    })
    .catch((error) => console.error("Error rescanning URL:", error));
  };

  return (
    <div className="dashboard">
      <header className="header">
        {/* URL Analyzer Logo */}
        <img src="/images/UrlCheckingSystem.png" alt="URL Checking System" className="url-analyzer-logo" />
      </header>

      {/* Page Title */}
      <h2 className="saved-urls-title">Your Saved URLs</h2>

      {/* Action Buttons and Input Field */}
      <div className="url-actions-container">
        <div className="url-actions">
          <input
            type="text"
            placeholder="Enter a new URL"
            value={newUrl}
            onChange={(e) => setNewUrl(e.target.value)}
            className="url-input"
          />
          <button className="rescan-btn" onClick={handleAddUrl}>Add URL</button>
          <button className="edit-btn" onClick={handleEditSelected}>Edit Selected</button>
          <button className="rescan-btn" onClick={handleRescan}>Rescan Selected</button>
          <button className="delete-btn" onClick={handleDelete} style={{ backgroundColor: "#f44739", color: "white" }}>
            Delete Selected
          </button>
        </div>
      </div>

      {/* URL Table */}
      <table>
        <thead>
          <tr>
            <th>Select</th>
            <th>URL</th>
            <th>SSL Certificates</th>
            <th>Response Code</th>
            <th>Response Headers</th>
            <th>SSL Protocol/Cipher Used</th>
          </tr>
        </thead>
        <tbody>
          {savedUrls.map((savedUrl) => (
            <tr key={savedUrl.url_id}>
              <td>
                <input
                  type="checkbox"
                  checked={savedUrl.selected || false}
                  onChange={() => handleCheckboxChange(savedUrl.url_id)}
                />
              </td>
              <td className="table-cell" id={`url-${savedUrl.url_id}`} contentEditable>
                {savedUrl.url}
              </td>
              <td className="table-cell" data-full-text={savedUrl.ssl || "N/A"}>
                {savedUrl.ssl || "N/A"}
              </td>
              <td className="table-cell" data-full-text={savedUrl.resCode || "N/A"}>
                {savedUrl.resCode || "N/A"}
              </td>
              <td className="table-cell expandable" dangerouslySetInnerHTML={{ __html: formatHeadersForDisplay(savedUrl.resHead) }}>
              </td>
              <td className="table-cell" data-full-text={savedUrl.sslPro || "N/A"}>
                {savedUrl.sslPro || "N/A"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Dashboard;
