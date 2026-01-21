import React, { useState } from "react";
import axios from "axios";

function FileUpload() {
  const [files, setFiles] = useState([]);
  const [keywords, setKeywords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleFileChange = (e) => {
    setFiles(e.target.files);   // FileList (multiple)
    setKeywords([]);
    setError("");
  };

  const handleUpload = async () => {
    if (!files || files.length === 0) {
      setError("Please select at least one PDF or TXT file");
      return;
    }

    const formData = new FormData();

    // 🔑 MUST match backend: @RequestParam("files")
    for (let i = 0; i < files.length; i++) {
      formData.append("files", files[i]);
    }

    try {
      setLoading(true);
      setError("");

      const response = await axios.post(
        "http://localhost:8080/api/analyze",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          validateStatus: () => true, // we handle status manually
        }
      );

      if (response.status === 200) {
        setKeywords(response.data);
      } else if (response.status === 400) {
        setError("No files received by backend");
      } else {
        setError("Failed to analyze uploaded files");
      }

    } catch (err) {
      console.error(err);
      setError("Unable to connect to backend");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-xl bg-white rounded-xl shadow-lg p-8">

        <h2 className="text-2xl font-bold text-slate-800 text-center mb-2">
          📄 Exam Question Pattern Analyzer
        </h2>

        <p className="text-sm text-slate-500 text-center mb-6">
          Upload one or more exam papers (PDF / TXT).
        </p>

        {/* File Upload */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Upload Question Papers
          </label>

          <input
            type="file"
            accept=".pdf,.txt"
            multiple
            onChange={handleFileChange}
            className="block w-full text-sm text-slate-600
                       file:mr-4 file:py-2 file:px-4
                       file:rounded-md file:border-0
                       file:text-sm file:font-semibold
                       file:bg-blue-50 file:text-blue-700
                       hover:file:bg-blue-100"
          />

          {files.length > 0 && (
            <p className="mt-1 text-xs text-slate-500">
              {files.length} file(s) selected
            </p>
          )}
        </div>

        {/* Upload Button */}
        <button
          onClick={handleUpload}
          disabled={loading}
          className={`w-full py-2 rounded-md font-semibold transition
            ${loading
              ? "bg-blue-400 cursor-not-allowed"
              : "bg-blue-600 hover:bg-blue-700 text-white"
            }`}
        >
          {loading ? "Analyzing..." : "Upload & Analyze"}
        </button>

        {/* Error Message */}
        {error && (
          <p className="mt-4 text-sm text-red-600 text-center">
            {error}
          </p>
        )}

        {/* Results Section */}
        <div className="mt-8">
          <h3 className="text-lg font-semibold text-slate-800 mb-3">
            🔑 Important Questions
          </h3>

          {keywords.length === 0 && !loading && !error && (
            <p className="text-sm text-slate-500">
              No analysis results yet.
            </p>
          )}

          <ul className="space-y-3">
            {keywords.map((item, index) => (
              <li
                key={index}
                className="p-3 border border-slate-200 rounded-md
                           flex justify-between gap-4"
              >
                <span className="text-slate-700 text-sm">
                  {item.question}
                </span>

                <span className="text-blue-600 font-semibold text-sm whitespace-nowrap">
                  {item.probability.toFixed(2)}%
                </span>
              </li>
            ))}
          </ul>
        </div>

      </div>
    </div>
  );
}

export default FileUpload;
