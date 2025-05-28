<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Support\Facades\Auth;

class CheckBearerToken
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        // Check if the Authorization header is present
        $authHeader = $request->header('Authorization');

        if (!$authHeader || !str_starts_with($authHeader, 'Bearer ')) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Missing or invalid Authorization header.',
            ], 401); // Unauthorized
        }

        // Extract the token
        $token = str_replace('Bearer ', '', $authHeader);

        // Attempt to authenticate with the token
        if (!Auth::check() || Auth::user()->token() !== $token) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized. Invalid or expired token.',
            ], 401); // Unauthorized
        }

        return $next($request);
    }
}
