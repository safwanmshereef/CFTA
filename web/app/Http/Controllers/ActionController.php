<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use App\Models\fuel_type;
use App\Models\transmission_type;
use App\Models\modeoftranspotation;

class ActionController extends Controller
{
    // Handle the login request
    public function login(Request $request)
    {
        // Validate the email and password fields
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        // Find the user by email
        $user = User::where('email', $request->email)->first();

        // If user does not exist, return an error
        if (!$user) {
            return redirect()->route('index.login')->withErrors(['email' => 'User not found.']);
        }

        // Check if the password is correct
        if (!Hash::check($request->password, $user->password)) {
            return redirect()->route('index.login')->withErrors(['password' => 'Invalid password.']);
        }

        // Log the user in and create a session
        Auth::login($user);

        // Redirect the user to the home page
        return redirect()->route('home');
    }

    public function postcreatefueltype(Request $request)
    {
        // Validate the form input: Transmission Type should be required and a string
        $validatedData = $request->validate([
            'name' => 'required|string|max:255',
        ]);

        fuel_type::create([
            'name' => $validatedData['name'], // Assuming you have a 'name' column in the 'users' table
        ]);

        // Return success response or redirect
        return redirect()->route('fueltype')->with('success', 'Transmission Type added successfully!');
    }

    public function postcreatetransmissiontype(Request $request)
    {
        // Validate the form input: Transmission Type should be required and a string
        $validatedData = $request->validate([
            'name' => 'required|string|max:255',
        ]);

        transmission_type::create([
            'name' => $validatedData['name'], // Assuming you have a 'name' column in the 'users' table
        ]);

        // Return success response or redirect
        return redirect()->route('transmissiontype')->with('success', 'Transmission Type added successfully!');
    }
    
    public function postcreatemodeoftranspotations(Request $request)
    {
        // Validate the form input: Transmission Type should be required and a string
        $validatedData = $request->validate([
            'name' => 'required|string|max:255',
        ]);

        modeoftranspotation::create([
            'name' => $validatedData['name'], // Assuming you have a 'name' column in the 'users' table
        ]);

        // Return success response or redirect
        return redirect()->route('modeoftranspotations')->with('success', 'Mode of transpotations added successfully!');
    }
}
