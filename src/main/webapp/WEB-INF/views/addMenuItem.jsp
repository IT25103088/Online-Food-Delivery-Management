<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Menu Item</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: Arial, sans-serif;
            background: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .card {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            width: 100%;
            max-width: 480px;
        }

        h2 {
            margin-bottom: 1.5rem;
            color: #333;
            border-bottom: 2px solid #e74c3c;
            padding-bottom: 0.5rem;
        }

        .form-group {
            margin-bottom: 1.2rem;
        }

        label {
            display: block;
            margin-bottom: 0.3rem;
            font-weight: bold;
            color: #555;
            font-size: 0.9rem;
        }

        input[type="text"],
        input[type="number"],
        textarea {
            width: 100%;
            padding: 0.6rem 0.8rem;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 1rem;
            transition: border-color 0.2s;
        }

        input:focus, textarea:focus {
            outline: none;
            border-color: #e74c3c;
        }

        textarea {
            resize: vertical;
            min-height: 80px;
        }

        .btn-submit {
            width: 100%;
            padding: 0.75rem;
            background: #e74c3c;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 1rem;
            cursor: pointer;
            margin-top: 0.5rem;
        }

        .btn-submit:hover {
            background: #c0392b;
        }

        .msg-success {
            background: #d4edda;
            color: #155724;
            padding: 0.75rem;
            border-radius: 4px;
            margin-bottom: 1rem;
            font-size: 0.9rem;
        }

        .msg-error {
            background: #f8d7da;
            color: #721c24;
            padding: 0.75rem;
            border-radius: 4px;
            margin-bottom: 1rem;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>

<div class="card">
    <h2>Add Menu Item</h2>

    <% if (request.getAttribute("success") != null) { %>
        <div class="msg-success"><%= request.getAttribute("success") %></div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="msg-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form action="addMenuItem" method="post">

        <div class="form-group">
            <label for="restaurantName">Restaurant Name</label>
            <input type="text" id="restaurantName" name="restaurantName"
                   placeholder="e.g. Pizza Palace" required />
        </div>

        <div class="form-group">
            <label for="itemName">Item Name</label>
            <input type="text" id="itemName" name="itemName"
                   placeholder="e.g. Margherita Pizza" required />
        </div>

        <div class="form-group">
            <label for="price">Price ($)</label>
            <input type="number" id="price" name="price"
                   placeholder="e.g. 12.99" step="0.01" min="0" required />
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description"
                      placeholder="Brief description of the item..." required></textarea>
        </div>

        <input type="submit" class="btn-submit" value="Add Menu Item" />

    </form>
</div>

</body>
</html>