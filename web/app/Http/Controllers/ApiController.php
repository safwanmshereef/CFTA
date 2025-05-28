<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\fuel_type;
use App\Models\transmission_type;
use App\Models\User;
use App\Models\car;
use App\Models\status;
use App\Models\Ranking;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\ValidationException;
use Illuminate\Database\Eloquent\ModelNotFoundException;
use Illuminate\Database\QueryException;
use Carbon\Carbon;



class ApiController extends Controller
{
    public function getallfueltypes()
    {
        try {
            $fuelTypes = fuel_type::all();
    
            if ($fuelTypes->isEmpty()) {
                return response()->json([
                    'success' => false,
                    'message' => 'No fuel types found.'
                ], 404);
            }
    
            return response()->json([
                'success' => true,
                'data' => $fuelTypes
            ]);
    
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while fetching fuel types.',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    public function getalltransmissiontypes()
    {
        try {
            $transmissionTypes = transmission_type::all();

            if ($transmissionTypes->isEmpty()) {
                return response()->json([
                    'success' => false,
                    'message' => 'No transmission types found.'
                ], 404);
            }

            return response()->json([
                'success' => true,
                'data' => $transmissionTypes
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while fetching transmission types.',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    public function getallstatus()
    {
        try {
            // Fetch all status records from the database
            $statuses = status::all();

            // Check if statuses are found
            if ($statuses->isEmpty()) {
                return response()->json([
                    'success' => false,
                    'message' => 'No status records found.',
                ], 404);  // Not Found
            }

            // Return a success response with the status data
            return response()->json([
                'success' => true,
                'message' => 'Statuses fetched successfully.',
                'data' => $statuses,
            ], 200);  // OK
        } catch (\Exception $e) {
            // Catch any unexpected errors and return a server error
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while fetching statuses.',
                'error' => $e->getMessage(),
            ], 500);  // Internal Server Error
        }
    }

    public function login(Request $request)
    {
        // Validate the input
        $this->validate($request, [
            'email' => 'required|email',      // Ensure email is provided and is valid
            'password' => 'required|string',  // Ensure password is provided
        ]);

        // Check if the email and password are correct
        $user = User::where('email', $request->email)->first();

        // If user is not found or password is incorrect
        if (!$user || !Hash::check($request->password, $user->password)) {
            return response()->json([
                'success' => false,
                'message' => 'Invalid credentials. Please check your email and password.'
            ], 401);
        }

        // Generate the authentication token (Optional: if using Passport or Sanctum)
        $token = $user->createToken('YourAppName')->accessToken;

        // Respond with a success message and the token
        return response()->json([
            'success' => true,
            'message' => 'Login successful.',
            'data' => [
                'user' => $user,
                'token' => $token,
            ]
        ], 200);
    }

    public function register(Request $request)
    {
        // Validate input data
        $this->validate($request, [
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',  // Ensure email is unique in the database
            'password' => 'required|string|confirmed',  // Ensure password is confirmed (password_confirmation field)
            'password_confirmation' => 'required|string',  // Ensure password confirmation field is provided
        ]);

        // Check if the email already exists in the database
        if (User::where('email', $request->email)->exists()) {
            return response()->json([
                'success' => false,
                'message' => 'Email already exists.',
            ], 409);  // Conflict
        }

        // Create the new user
        try {
            $user = User::create([
                'name' => $request->name,
                'email' => $request->email,
                'password' => Hash::make($request->password),  // Encrypt password
            ]);

            // Generate the authentication token (Optional: if using Passport or Sanctum)
            $token = $user->createToken('YourAppName')->accessToken;

            // Respond with a success message and the token
            return response()->json([
                'success' => true,
                'message' => 'User registered successfully.',
                'data' => [
                    'user' => $user,
                    'token' => $token,
                ]
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred during registration.',
                'error' => $e->getMessage(),
            ], 500);  // Internal Server Error
        }
    }

    public function profile()
    {
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }

        // Fetch authenticated user's data
        $user = Auth::user();

        return response()->json([
            'success' => true,
            'message' => 'User profile fetched successfully.',
            'data' => $user,
        ], 200);  // OK
    }

    public function updateprofile(Request $request)
    {
        // Ensure the user is authenticated
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }

        // Fetch authenticated user
        $user = Auth::user();

        // Validate the input data
        $validator = Validator::make($request->all(), [
            'name' => 'required|string|max:255'
        ]);

        // If validation fails, return errors
        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed.',
                'errors' => $validator->errors(),
            ], 422);  // Unprocessable Entity
        }

        try {
            // Update the user's profile data
            $user->name = $request->name;
            $user->save();

            return response()->json([
                'success' => true,
                'message' => 'User profile updated successfully.',
                'data' => $user,
            ], 200);  // OK
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while updating the profile.',
                'error' => $e->getMessage(),
            ], 500);  // Internal Server Error
        }
    }

    public function changepassword(Request $request)
    {
        // Ensure the user is authenticated (validate the token)
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }

        // Fetch the authenticated user
        $user = Auth::user();

        // Validate the input data
        $validator = Validator::make($request->all(), [
            'new_password' => 'required|string|confirmed',
            'new_password_confirmation' => 'required|string',
        ]);

        // If validation fails, return the errors
        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed.',
                'errors' => $validator->errors(),
            ], 422);  // Unprocessable Entity
        }

        // Update the password
        try {
            // Set the new password after hashing
            $user->password = Hash::make($request->new_password);
            $user->save();

            return response()->json([
                'success' => true,
                'message' => 'Password updated successfully.',
            ], 200);  // OK
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while updating the password.',
                'error' => $e->getMessage(),
            ], 500);  // Internal Server Error
        }
    }

    public function createcar(Request $request)
    {
        // Ensure the user is authenticated (validate the token)
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }

        // Fetch authenticated user
        $user = Auth::user();
        // Validate the input data for creating a car
        $validator = Validator::make($request->all(), [
            'make' => 'required|string|max:255',
            'model' => 'required|string|max:255',
            'engine_size' => 'required|string|max:255',
            'transmission_type' => 'required|exists:transmission_types,tran_id',
            'fuel_type' => 'required|exists:fuel_types,fuel_id',
            'milage' => 'required|string|min:0',
            'co2' => 'required|string|min:0',
        ]);
        

        // If validation fails, return errors
        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed.',
                'errors' => $validator->errors(),
            ], 422);  // Unprocessable Entity
        }

        // Check if the provided transmission type exists in the database
        if (!\DB::table('transmission_types')->where('tran_id', $request->transmission_type)->exists()) {
            return response()->json([
                'success' => false,
                'message' => 'Invalid transmission type.',
            ], 400);  // Bad Request
        }

        // Check if the provided fuel type exists in the database
        if (!\DB::table('fuel_types')->where('fuel_id', $request->fuel_type)->exists()) {
            return response()->json([
                'success' => false,
                'message' => 'Invalid fuel type.',
            ], 400);  // Bad Request
        }

        // Create the car record
        try {
            // $car = car::create([
            //     'user_id' => $user->id,
            //     'make' => $request->make,
            //     'model' => $request->model,
            //     'engine_size' => $request->engine_size,
            //     'transmission_type' => $request->transmission_type,
            //     'fuel_type' => $request->fuel_type,
            //     'milage' => $request->milage,
            //     'co2' => $request->co2,
            //     'status_id' => "1",
            // ]);
            $car = new car();
            $car->user_id = $user->id;
            $car->make = $request->make;
            $car->model = $request->model;
            $car->engine_size = $request->engine_size;
            $car->transmission_type = $request->transmission_type;
            $car->fuel_type = $request->fuel_type;
            $car->milage = $request->milage; // Assuming the correct spelling is "mileage"
            $car->co2 = $request->co2;
            $car->status_id = '1';
            if($car->save()){
                return response()->json([
                    'success' => true,
                    'message' => 'car created successfully.',
                    'data' => $car,
                ], 201);  // Created
            }else{
                return response()->json([
                    'success' => false,
                    'message' => 'An error occurred while creating the car.',
                    'error' => $e->getMessage(),
                ], 500);
            }
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while creating the car.',
                'error' => $e->getMessage(),
            ], 500);  // Internal Server Error
        }
    }
    
    public function updatecar(Request $request, $car_id)
    {
        // Ensure the user is authenticated (validate the token)
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }

        // Fetch the authenticated user
        $user = Auth::user();

        // Fetch the car record from the database
        $car = car::find($car_id);

        // Check if the car exists and belongs to the authenticated user
        if (!$car) {
            return response()->json([
                'success' => false,
                'message' => 'car not found.',
            ], 404);  // Not Found
        }

        if ($car->user_id !== $user->user_id) {
            return response()->json([
                'success' => false,
                'message' => 'You do not have permission to update this car.',
            ], 403);  // Forbidden
        }

        // Validate the input data for updating the car
        $validator = Validator::make($request->all(), [
            'make' => 'nullable|string|max:255',
            'model' => 'nullable|string|max:255',
            'engine_size' => 'nullable|string|max:255',
            'transmission_type' => 'nullable|exists:transmission_types,tran_id',
            'fuel_type' => 'nullable|exists:fuel_types,fuel_id',
            'milage' => 'nullable|min:0',
            'co2' => 'nullable|min:0',
            'status_id' => 'nullable|exists:statuses,status_id',
        ]);

        // If validation fails, return errors
        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed.',
                'errors' => $validator->errors(),
            ], 422);  // Unprocessable Entity
        }

        // Update the car record with validated data
        try {
            // Only update fields that are provided in the request
            $car->update($request->only([
                'make',
                'model',
                'engine_size',
                'transmission_type',
                'fuel_type',
                'milage',
                'co2',
                'status_id',
            ]));

            return response()->json([
                'success' => true,
                'message' => 'car updated successfully.',
                'data' => $car,
            ], 200);  // OK
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while updating the car.',
                'error' => $e->getMessage(),
            ], 500);  // Internal Server Error
        }
    }

    public function createroute(Request $request)
    {
        // Ensure the user is authenticated (validate the token)
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }
        // Validate the input data
        $validator = Validator::make($request->all(), [
            'mot' => 'required',
            'vehicle_id' => 'required',
            'transport' => 'required',
            'from_lat' => 'required',
            'from_long' => 'required',
            'start_date_time' => 'required',
        ]);



        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed.',
                'errors' => $validator->errors(),
            ], 422); // Unprocessable Entity
        }

        // Create the route
        $routeId = DB::table('travel')->insertGetId([
            'user_id' => Auth::user()->id,
            'mot' => $request->mot,
            'vehicle_id' => $request->vehicle_id,
            'transport' => $request->transport,
            'from_lat' => $request->from_lat,
            'from_long' => $request->from_long,
            'start_date_time' => $request->start_date_time,
            'status' => '1',
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Route created successfully.',
            'data' => $routeId,
        ], 201); // Created
    }

    public function endroutes(Request $request)
    {
       // Ensure the user is authenticated (validate the token)
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }

        // Validate the input data
        $validator = Validator::make($request->all(), [
            'gprs' => 'required',
            'sms' => 'required',
            'phone' => 'required',
            'to_lat' => 'required',
            'to_long' => 'required',
            'end_date_time' => 'required',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validation failed.',
                'errors' => $validator->errors(),
            ], 422); // Unprocessable Entity
        }

        // Insert into `travelusages` table and get the inserted ID
        $usageId = DB::table('travelusages')->insertGetId([
            'gprs' => $request->gprs,
            'sms' => $request->sms,
            'phone' => $request->phone,
            'created_at' => now(),
            'updated_at' => now(),
        ]);

        // Update `travel` table with the retrieved usage_id
        $update = DB::table('travel')
            ->where('id', $request->travel_id) // Assuming travel_id is passed in the request
            ->update([
                'to_lat' => $request->to_lat,
                'to_long' => $request->to_long,
                'end_date_time' => $request->end_date_time,
                'usage_id' => $usageId,
                'status' => '2',
                'updated_at' => now(),
            ]);

        if ($update) {
            return response()->json([
                'success' => true,
                'message' => 'Travel record updated successfully.',
                'usage_id' => $usageId,
            ], 200);
        } else {
            return response()->json([
                'success' => false,
                'message' => 'Failed to update travel record.',
            ], 500);
        }

    }

    public function userroutes()
    {
        // Ensure the user is authenticated (validate the token)
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401);  // Unauthorized
        }
        // Ensure the user is authenticated
        $user = Auth::user();

        // Fetch all routes for the authenticated user
        $routes = Route::where('user_id', $user->user_id)->get();

        // Check if routes are found
        if ($routes->isEmpty()) {
            return response()->json([
                'success' => false,
                'message' => 'No routes found for this user.',
            ], 404); // Not Found
        }

        return response()->json([
            'success' => true,
            'message' => 'Routes fetched successfully.',
            'data' => $routes,
        ], 200); // OK
    }

    public function getCarsByUser(Request $request)
    {
        // $cars = Car::with([
        //     'transmissionType', 
        //     'fuelType', 
        //     'status', 
        //     'user'
        // ])
        // ->get([
        //     'car_id', 
        //     'make', 
        //     'model', 
        //     'engine_size', 
        //     'transmission_type', 
        //     'fuel_type', 
        //     'milage', 
        //     'co2', 
        //     'status_id', 
        //     'created_at', 
        //     'updated_at'
        // ]);
        $cars = DB::table('cars as c')
        ->leftJoin('transmission_types as tt', 'c.transmission_type', '=', 'tt.tran_id')
        ->leftJoin('fuel_types as ft', 'c.fuel_type', '=', 'ft.fuel_id')
        ->leftJoin('statuses as s', 'c.status_id', '=', 's.status_id')
        ->leftJoin('users as u', 'c.user_id', '=', 'u.id')
        ->select(
            'c.car_id as car_id',
            'c.user_id',
            'u.name as username',
            'c.make',
            'c.model',
            'c.engine_size',
            'c.milage',
            'c.co2',
            'c.transmission_type',
            'tt.name as transmission_type_name',
            'c.fuel_type',
            'ft.name as fuel_type_name',
            'c.status_id',
            's.name as status_name',
            'c.created_at',
            'c.updated_at'
        )
        ->get();
        return response()->json($cars);
    }

    public function getCars(Request $request)
    {
        try {
            // Ensure the authenticated user is valid
            $user = Auth::user();
            if (!$user) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized access. Invalid token.',
                ], 401);
            }

            // Fetch cars with the required query
            $cars = DB::table('cars as c')
                ->leftJoin('transmission_types as tt', 'c.transmission_type', '=', 'tt.tran_id')
                ->leftJoin('fuel_types as ft', 'c.fuel_type', '=', 'ft.fuel_id')
                ->select(
                    'c.car_id',
                    'c.user_id',
                    'c.make',
                    'c.model',
                    'c.engine_size',
                    'c.transmission_type as transmission_type_id',
                    'tt.name as transmission_type_name',
                    'c.fuel_type as fuel_type_id',
                    'ft.name as fuel_type_name',
                    'c.milage',
                    'c.co2',
                    'c.prediction',
                    'c.status_id',
                    'c.emition',
                    'c.created_at',
                    'c.updated_at'
                )
                ->where('c.user_id', $user->id)
                ->get();

            return response()->json([
                'success' => true,
                'message' => 'Cars fetched successfully.',
                'data' => $cars->toArray(),
            ], 200);
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'An error occurred while fetching cars.',
                'error' => $e->getMessage(),
            ], 500);
        }
    }

    public function useractiveroutes(Request $request)
    {
       // Check if the user is authenticated
        if (Auth::guest()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized access. Invalid token.',
            ], 401);
        }

        try {
            // Get the authenticated user's ID
            $userId = Auth::user()->id;

            // Query the travel table
            $data = DB::table('travel as t')
                ->select('t.id', 't.user_id', 't.mot', 't.vehicle_id', 'c.make', 'c.model', 'c.engine_size', 't.transport', 
                        't.from_lat', 't.from_long', 't.to_lat', 't.to_long', 't.start_date_time', 't.end_date_time', 
                        't.usage_id', 't.co2', 't.media_id', 't.status', 't.created_at', 't.updated_at')
                ->leftJoin('cars as c', function ($join) {
                    $join->on('t.vehicle_id', '=', 'c.car_id')
                        ->where('t.mot', '=', 'Own');
                })
                ->where('t.status', "1")
                ->where('t.user_id', $userId)
                ->get();

            // Return success response with the data
            return response()->json([
                'success' => true,
                'message' => 'Data retrieved successfully.',
                'data' => $data
            ], 200);

        } catch (\Exception $e) {
            // Return error response in case of an exception
            return response()->json([
                'success' => false,
                'message' => 'Error occurred: ' . $e->getMessage(),
            ], 500);
        }
    }

    public function updateemition(Request $request)
    {
        try {
            // Validate incoming request
            $validated = $request->validate([
                'user_id' => 'required|integer', // Ensure user_id is an integer
                'co2' => 'required|numeric', // Ensure co2 is a number
                'id' => 'required|numeric' // Ensure id is a number
            ]);
        
            // Step 1: Update the CO2 value in the travel table
            $updated = DB::table('travel')
                ->where('user_id', $validated['user_id'])
                ->where('id', $validated['id'])
                ->update(['co2' => $validated['co2']]);
        
            // If no records were updated, return a message
            if (!$updated) {
                return response()->json([
                    'message' => 'No record found or no changes made.'
                ], 404);
            }
        
            // Step 2: Calculate totalRanking for the user
            $totalRanking = DB::table('travel')
                ->where('user_id', $validated['user_id'])
                ->whereNotNull('co2')
                ->where('co2', '!=', '')
                ->sum(DB::raw("
                    CASE 
                        WHEN co2 > 250 THEN 1 
                        WHEN co2 BETWEEN 200 AND 249 THEN 2
                        WHEN co2 BETWEEN 150 AND 199 THEN 3
                        WHEN co2 BETWEEN 100 AND 149 THEN 4
                        ELSE 5
                    END
                "));
        
            // Step 3: Get existing rank from the rankings table
            $existingRank = DB::table('rankings')
                ->where('user_id', $validated['user_id'])
                ->value('rank'); // Fetch only the rank column
        
            $existingRank = $existingRank ?? 0; // If no rank exists, default to 0
        
            // Step 4: Update the rankings table with (existing rank + current rank)
            DB::table('rankings')
                ->updateOrInsert(
                    ['user_id' => $validated['user_id']], // Match user_id
                    [
                        'rank' => $existingRank + $totalRanking, // Add existing + new ranking
                        'updated_at' => now(),
                        'created_at' => now(), // Only used if inserting
                    ]
                );
        
            // Return response with success message and updated ranking
            return response()->json([
                'message' => 'Record updated successfully.',
                'total_ranking' => $totalRanking,
                'updated_rank' => $existingRank + $totalRanking
            ], 200);
        
        } catch (\Exception $e) {
            // Handle errors
            return response()->json([
                'message' => 'An error occurred: ' . $e->getMessage()
            ], 500);
        }
        
    }

    public function getTraveldetailsForML()
    {
        try {
            // Execute the query
            $travelDetails = DB::select("
                SELECT t.`id`, t.`user_id`, u.name AS user_name, 
                       t.`mot`, t.`vehicle_id`, c.make, c.model, c.engine_size, 
                       c.transmission_type, c.fuel_type, c.milage, c.co2, c.emition,c.prediction, 
                       t.`transport`, t.`from_lat`, t.`from_long`, 
                       t.`to_lat`, t.`to_long`, t.`start_date_time`, 
                       t.`end_date_time`, t.`usage_id`, tu.gprs, tu.sms, 
                       tu.phone, t.`co2`, t.`media_id`, t.`status`, 
                       t.`created_at`, t.`updated_at`
                FROM `travel` t 
                LEFT JOIN `users` u ON t.user_id = u.id 
                LEFT JOIN `cars` c ON t.vehicle_id = c.car_id 
                LEFT JOIN `travelusages` tu ON t.usage_id = tu.id
                WHERE t.co2 IS NULL
            ");

            // Return success response
            return response()->json([
                'status' => 'success',
                'data' => $travelDetails
            ], 200);
        } catch (Exception $e) {
            // Log error
            \Log::error('Error fetching travel details: ' . $e->getMessage());

            // Return error response
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to fetch travel details',
                'error' => $e->getMessage()
            ], 500);
        }
    }
    
    public function getRankingList()
    {
        try {
            // Get first and last date of the current month
            $firstDayOfMonth = date('Y-m-01 00:00:00');
            $lastDayOfMonth = date('Y-m-t 23:59:59');

            // Execute the query: Get ranking details for the current month
            $rankingDetails = DB::select("
                SELECT u.id, u.name, u.email, 
                    SUM(r.rank) AS rank, -- Sum rank per user
                    MAX(t.updated_at) AS last_updated -- Last updated timestamp
                FROM `travel` t 
                LEFT JOIN `users` u ON t.user_id = u.id 
                LEFT JOIN `rankings` r ON t.user_id = r.user_id
                WHERE t.co2 IS NOT NULL 
                GROUP BY u.id, u.name, u.email
                ORDER BY rank ASC -- Less rank first
            ");

            // If no ranking data is found
            if (empty($rankingDetails)) {
                return response()->json([
                    'status' => 'success',
                    'data' => [],
                    'first' => null,
                    'second' => null,
                    'third' => null
                ], 200);
            }

            // Get first, second, and third ranked users
            $first = $rankingDetails[0] ?? null;
            $second = $rankingDetails[1] ?? null;
            $third = $rankingDetails[2] ?? null;

            // Return ranking list with top 3 users separately
            return response()->json([
                'status' => 'success',
                'data' => $rankingDetails, // Full sorted ranking list
                'first' => $first,          // User with lowest rank
                'second' => $second,        // Second lowest rank
                'third' => $third           // Third lowest rank
            ], 200);

        } catch (Exception $e) {
            // Log error
            \Log::error('Error fetching ranking list: ' . $e->getMessage());

            // Return error response
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to fetch ranking list',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    public function getMonthlyCo2()
    {
        // Check if the user is authenticated
        if (!Auth::check()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized access. Invalid token.',
            ], 401);
        }

        try {
            // Get the authenticated user's ID
            $userId = Auth::id();

            // Get the start and end dates of the current month
            $startOfMonth = Carbon::now()->startOfMonth()->toDateString();
            $endOfMonth = Carbon::now()->endOfMonth()->toDateString();

            // Fetch CO2 values for the current month, ignoring null values
            $co2Data = DB::table('travel')
                ->select(DB::raw('DATE(created_at) as date, SUM(co2) as total_co2'))
                ->where('user_id', $userId)
                ->whereNotNull('co2')
                ->groupBy('date')
                ->orderBy('date', 'ASC')
                ->get();

            // Format response data
            $formattedData = [];
            foreach ($co2Data as $data) {
                $formattedData[] = [
                    'date' => $data->date,
                    'co2' => $data->total_co2
                ];
            }

            return response()->json([
                'success' => true,
                'message' => 'CO2 data retrieved successfully.',
                'data' => $formattedData
            ], 200);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Failed to fetch CO2 data.',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    public function getRankingTravelDetails(Request $request){
        try {
            // Execute the query
            $travelDetails = DB::select("
                SELECT t.`id`, t.`user_id`, u.name AS user_name, 
                       t.`mot`, t.`vehicle_id`, c.make, c.model, c.engine_size, 
                       c.transmission_type, c.fuel_type, c.milage, c.co2, c.emition, 
                       t.`transport`, t.`from_lat`, t.`from_long`, 
                       t.`to_lat`, t.`to_long`, t.`start_date_time`, 
                       t.`end_date_time`, t.`usage_id`, tu.gprs, tu.sms, 
                       tu.phone, t.`co2`, t.`media_id`, t.`status`, 
                       t.`created_at`, t.`updated_at`,
                       CASE 
                           WHEN t.co2 BETWEEN 250 AND 300 THEN 1
                           WHEN t.co2 BETWEEN 200 AND 249 THEN 2
                           WHEN t.co2 BETWEEN 150 AND 199 THEN 3
                           WHEN t.co2 BETWEEN 100 AND 149 THEN 4
                           ELSE 5
                       END AS ranking
                FROM `travel` t 
                LEFT JOIN `users` u ON t.user_id = u.id 
                LEFT JOIN `cars` c ON t.vehicle_id = c.car_id 
                LEFT JOIN `travelusages` tu ON t.usage_id = tu.id
                WHERE c.prediction IS NOT NULL
                AND t.co2 IS NOT NULL 
                AND t.co2 != '' 
                AND MONTH(t.updated_at) = MONTH(CURRENT_DATE()) 
                AND YEAR(t.updated_at) = YEAR(CURRENT_DATE())
                ORDER BY t.co2 ASC
            ");

            // Return success response
            return response()->json([
                'status' => 'success',
                'data' => $travelDetails
            ], 200);
        } catch (Exception $e) {
            // Log error
            \Log::error('Error fetching travel details: ' . $e->getMessage());

            // Return error response
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to fetch travel details',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    public function getMyRank(Request $request){
        try {
            if (!Auth::check()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Unauthorized. Invalid or expired token.',
                ], 401);  // Unauthorized
            }
    
            // Fetch authenticated user's data
            $user = Auth::user();
            
            // Fetch ranking details for the given user_id
            $ranking = DB::table('rankings')
                ->select('id', 'user_id', 'rank', 'created_at', 'updated_at')
                ->where('user_id', $user->id)
                ->first(); // Get single record
    
            // Check if data exists
            if (!$ranking) {
                return response()->json([
                    'success' => false,
                    'data'=> null,
                    'message' => 'No ranking found for this user.'
                ], 201);
            }
    
            // Return success response
            return response()->json([
                'status' => 'success',
                'data' => $ranking
            ], 200);
    
        } catch (\Exception $e) {
            // Handle errors
            return response()->json([
                'message' => 'An error occurred: ' . $e->getMessage()
            ], 500);
        }
    }

    public function getMyRoutes(Request $request){
        // Check if the user is authenticated
        if (Auth::guest()) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized access. Invalid token.',
            ], 401);
        }

        try {
            // Get the authenticated user's ID
            $userId = Auth::user()->id;

            // Query the travel table
            $data = DB::table('travel as t')
                ->select('t.id', 't.user_id', 't.mot', 't.vehicle_id', 'c.make', 'c.model', 'c.engine_size', 't.transport', 
                        't.from_lat', 't.from_long', 't.to_lat', 't.to_long', 't.start_date_time', 't.end_date_time', 
                        't.usage_id', 't.co2','c.co2 as car_co2' , 't.media_id', 't.status','tu.gprs','tu.sms','tu.phone','t.created_at', 't.updated_at')
                ->leftJoin('cars as c', function ($join) {
                    $join->on('t.vehicle_id', '=', 'c.car_id')
                        ->where('t.mot', '=', 'Own');
                })
                ->leftJoin('travelusages as tu', 't.usage_id', '=', 'tu.id')
                ->where('t.user_id', $userId)
                ->get();

            // Return success response with the data
            return response()->json([
                'success' => true,
                'message' => 'Data retrieved successfully.',
                'data' => $data
            ], 200);

        } catch (\Exception $e) {
            // Return error response in case of an exception
            return response()->json([
                'success' => false,
                'message' => 'Error occurred: ' . $e->getMessage(),
            ], 500);
        }
    }

    public function radeemPoints(Request $request){
        // Check if the user is authenticated
    if (Auth::guest()) {
        return response()->json([
            'success' => false,
            'message' => 'Unauthorized access. Invalid token.',
        ], 401);
    }

    try {
        // Get the authenticated user's ID
        $userId = Auth::user()->id;

        // Update the rankings table
        $affectedRows = DB::table('rankings')
            ->where('user_id', $userId)
            ->update([
                'rank' => '0',
                'updated_at' => now(), // Laravel's helper function for the current timestamp
            ]);

        // Check if any rows were affected
        if ($affectedRows === 0) {
            return response()->json([
                'success' => false,
                'message' => 'No records updated. User not found in rankings.',
            ], 404);
        }

        // Return success response
        return response()->json([
            'success' => true,
            'message' => 'Rank Redeem successfully.',
        ], 200);

    } catch (\Exception $e) {
        // Return error response in case of an exception
        return response()->json([
            'success' => false,
            'message' => 'Error occurred: ' . $e->getMessage(),
        ], 500);
    }
    }
}
