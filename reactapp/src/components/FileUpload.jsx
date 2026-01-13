import React, { useState } from "react";
import axios from "axios";

function FileUpload() {
  const [file, setFile] = useState(null);
  const [keywords, setKeywords] = useState([]);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      alert("Please select a file");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/analyze",
        formData,
        { headers: { "Content-Type": "multipart/form-data" } }
      );

      // ✅ IMPORTANT FIX
      setKeywords(response.data);

    } catch (error) {
      console.error(error);
      alert("Upload failed");
    }
  };

  return (
    <div>
      <h2>📄 Exam Question Pattern Analyzer</h2>

      <input type="file" accept=".pdf,.txt" onChange={handleFileChange} />
      <br /><br />

      <button onClick={handleUpload}>Upload & Analyze</button>

      <h3>🔑 Important Questions</h3>

      {keywords.length === 0 && <p>No data yet</p>}

      <ul>
        {keywords.map((item, index) => (
          <li key={index}>
            {item.question} → {item.probability.toFixed(2)}%
          </li>
        ))}
      </ul>
    </div>
  );
}

export default FileUpload;
