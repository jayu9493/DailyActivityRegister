const functions = require('firebase-functions');
const admin = require('firebase-admin');
const express = require('express');
const cors = require('cors');
const multer = require('multer');
const XLSX = require('xlsx');

admin.initializeApp();
const db = admin.firestore();

const app = express();
app.use(cors({ origin: true }));
app.use(express.json());

// Configure multer for file uploads
const upload = multer({ storage: multer.memoryStorage() });

// ============================================================================
// GET /api/android/projects - Get all projects
// ============================================================================
app.get('/api/android/projects', async (req, res) => {
    try {
        const projectsSnapshot = await db.collection('projects').get();
        const projects = [];

        projectsSnapshot.forEach(doc => {
            projects.push({
                id: doc.id,
                ...doc.data()
            });
        });

        res.json(projects);
    } catch (error) {
        console.error('Error getting projects:', error);
        res.status(500).json({ error: error.message });
    }
});

// ============================================================================
// POST /api/android/projects/create - Create new project
// ============================================================================
app.post('/api/android/projects/create', async (req, res) => {
    try {
        const {
            project_name,
            project_number,
            total_route_oh,
            total_route_ug,
            tower_count,
            line_passing_villages,
            subdivision,
            agencies
        } = req.body;

        // Calculate total route
        const total_route = (total_route_oh || 0) + (total_route_ug || 0);

        // Smart task creation
        const tasks = [
            { name: 'Survey', target: 100.0, current: 0.0, unit: '%' }
        ];

        // Add UG-specific tasks
        if (total_route_ug > 0) {
            tasks.push({
                name: 'Excavation',
                target: total_route_ug,
                current: 0.0,
                unit: 'km'
            });
        }

        // Add OH-specific tasks
        if (total_route_oh > 0) {
            tasks.push(
                {
                    name: 'Foundation',
                    target: tower_count || 0,
                    current: 0.0,
                    unit: 'Nos.'
                },
                {
                    name: 'Erection',
                    target: tower_count || 0,
                    current: 0.0,
                    unit: 'Nos.'
                },
                {
                    name: 'Stringing',
                    target: total_route_oh,
                    current: 0.0,
                    unit: 'km'
                }
            );
        }

        // Create project document
        const projectData = {
            project_name,
            project_number: project_number || null,
            total_route,
            total_route_oh: total_route_oh || 0,
            total_route_ug: total_route_ug || 0,
            tower_count: tower_count || 0,
            line_passing_villages: line_passing_villages || null,
            subdivision: subdivision || null,
            tasks,
            agencies: agencies || [],
            daily_logs: [],
            created_at: admin.firestore.FieldValue.serverTimestamp(),
            updated_at: admin.firestore.FieldValue.serverTimestamp(),
            status: 'active'
        };

        const docRef = await db.collection('projects').add(projectData);
        const doc = await docRef.get();

        res.json({
            id: doc.id,
            ...doc.data()
        });
    } catch (error) {
        console.error('Error creating project:', error);
        res.status(500).json({ error: error.message });
    }
});

// ============================================================================
// PUT /api/android/projects/:projectName - Update project
// ============================================================================
app.put('/api/android/projects/:projectName', async (req, res) => {
    try {
        const { projectName } = req.params;

        // Find project by name
        const projectsSnapshot = await db.collection('projects')
            .where('project_name', '==', projectName)
            .limit(1)
            .get();

        if (projectsSnapshot.empty) {
            return res.status(404).json({ error: 'Project not found' });
        }

        const projectDoc = projectsSnapshot.docs[0];
        const updateData = {
            ...req.body,
            updated_at: admin.firestore.FieldValue.serverTimestamp()
        };

        await projectDoc.ref.update(updateData);
        const updated = await projectDoc.ref.get();

        res.json({
            id: updated.id,
            ...updated.data()
        });
    } catch (error) {
        console.error('Error updating project:', error);
        res.status(500).json({ error: error.message });
    }
});

// ============================================================================
// POST /api/android/projects/upload - Upload Excel file
// ============================================================================
app.post('/api/android/projects/upload', upload.single('file'), async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ error: 'No file uploaded' });
        }

        // Parse Excel file
        const workbook = XLSX.read(req.file.buffer, { type: 'buffer' });
        const sheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[sheetName];
        const data = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

        // Extract project info (simplified - you'll need to adapt to your Excel structure)
        const projectName = data[1] ? data[1][1] : 'Imported Project';
        const projectNumber = data[2] ? data[2][1] : null;

        // Check if project already exists
        const existingSnapshot = await db.collection('projects')
            .where('project_name', '==', projectName)
            .limit(1)
            .get();

        if (!existingSnapshot.empty) {
            const existingDoc = existingSnapshot.docs[0];
            return res.json({
                id: existingDoc.id,
                ...existingDoc.data()
            });
        }

        // Create new project from Excel
        const projectData = {
            project_name: projectName,
            project_number: projectNumber,
            total_route: 0,
            total_route_oh: 0,
            total_route_ug: 0,
            tower_count: 0,
            tasks: [
                { name: 'Survey', target: 100.0, current: 0.0, unit: '%' }
            ],
            agencies: [],
            daily_logs: [],
            created_at: admin.firestore.FieldValue.serverTimestamp(),
            updated_at: admin.firestore.FieldValue.serverTimestamp(),
            status: 'active'
        };

        const docRef = await db.collection('projects').add(projectData);
        const doc = await docRef.get();

        res.json({
            id: doc.id,
            ...doc.data()
        });
    } catch (error) {
        console.error('Error uploading file:', error);
        res.status(500).json({ error: error.message });
    }
});

// Export the Express app as a Firebase Function
exports.api = functions.https.onRequest(app);
