#!/usr/bin/env node

/**
 * Firebase Functions Local Test Script
 * 
 * This script helps you test your Firebase Functions locally
 * before deploying to production.
 * 
 * Usage:
 *   node test-functions.js
 */

const axios = require('axios');

// Configuration
const LOCAL_URL = 'http://localhost:5001/dailyactivityregister/us-central1/api';
const PRODUCTION_URL = 'https://us-central1-dailyactivityregister.cloudfunctions.net/api';

// Use local by default, change to production after deploy
const BASE_URL = LOCAL_URL;

console.log('🔥 Firebase Functions Test Script\n');
console.log(`Testing against: ${BASE_URL}\n`);

// Test data
const testProject = {
    project_name: 'Test Project ' + Date.now(),
    project_number: 'TEST-001',
    total_route_oh: 5.0,
    total_route_ug: 3.5,
    tower_count: 25,
    subdivision: 'Test Subdivision',
    line_passing_villages: 'Village A, Village B',
    agencies: [
        { name: 'Test Agency', po_number: 'PO-001' }
    ]
};

async function runTests() {
    try {
        console.log('📋 Test 1: Get All Projects');
        console.log('GET /api/android/projects\n');

        const getResponse = await axios.get(`${BASE_URL}/api/android/projects`);
        console.log('✅ Success!');
        console.log(`Found ${getResponse.data.length} projects\n`);

        console.log('─'.repeat(50) + '\n');

        console.log('📋 Test 2: Create New Project');
        console.log('POST /api/android/projects/create\n');
        console.log('Sending:', JSON.stringify(testProject, null, 2));

        const createResponse = await axios.post(
            `${BASE_URL}/api/android/projects/create`,
            testProject
        );

        console.log('\n✅ Success!');
        console.log('Created project:', JSON.stringify(createResponse.data, null, 2));

        const projectName = createResponse.data.project_name;
        console.log('\n' + '─'.repeat(50) + '\n');

        console.log('📋 Test 3: Update Project');
        console.log(`PUT /api/android/projects/${projectName}\n`);

        const updateData = {
            tasks: [
                { name: 'Survey', target: 100, current: 50, unit: '%' },
                { name: 'Foundation', target: 25, current: 10, unit: 'Nos.' }
            ]
        };

        console.log('Sending:', JSON.stringify(updateData, null, 2));

        const updateResponse = await axios.put(
            `${BASE_URL}/api/android/projects/${encodeURIComponent(projectName)}`,
            updateData
        );

        console.log('\n✅ Success!');
        console.log('Updated project:', JSON.stringify(updateResponse.data, null, 2));

        console.log('\n' + '─'.repeat(50) + '\n');

        console.log('📋 Test 4: Get All Projects (Verify Update)');
        const finalGetResponse = await axios.get(`${BASE_URL}/api/android/projects`);
        console.log('✅ Success!');
        console.log(`Total projects: ${finalGetResponse.data.length}\n`);

        console.log('🎉 All tests passed!\n');

    } catch (error) {
        console.error('❌ Test failed!');

        if (error.response) {
            console.error('Status:', error.response.status);
            console.error('Data:', error.response.data);
        } else if (error.request) {
            console.error('No response received. Is the server running?');
            console.error('\nTo start local emulator:');
            console.error('  cd firebase-backend');
            console.error('  firebase emulators:start --only functions');
        } else {
            console.error('Error:', error.message);
        }

        process.exit(1);
    }
}

// Run tests
console.log('Starting tests...\n');
runTests();
