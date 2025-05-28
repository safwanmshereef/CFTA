<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ApiController;
use App\Http\Controllers\DataExportController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::get('fuel-types', [ApiController::class, 'getallfueltypes']);
Route::get('transmissiontypes', [ApiController::class, 'getalltransmissiontypes']);
Route::get('statuses', [ApiController::class, 'getallstatus']);
Route::post('login', [ApiController::class, 'login']);
Route::post('register', [ApiController::class, 'register']);

Route::get('gettraveldetailsforml', [ApiController::class, 'getTraveldetailsForML']);
Route::post('updateemition', [ApiController::class, 'updateemition']);

Route::middleware('auth:api')->get('profile', [ApiController::class, 'profile']);
Route::middleware('auth:api')->post('updateprofile', [ApiController::class, 'updateprofile']);
Route::middleware('auth:api')->post('change-password', [ApiController::class, 'changepassword']);
Route::middleware('auth:api')->post('create-car', [ApiController::class, 'createcar']);
Route::middleware('auth:api')->get('get-cars', [ApiController::class, 'getCars']);
Route::middleware('auth:api')->post('update-car/{car_id}', [ApiController::class, 'updatecar']);
Route::middleware('auth:api')->post('routes', [ApiController::class, 'createroute']);
Route::middleware('auth:api')->post('endroutes', [ApiController::class, 'endroutes']);
Route::middleware('auth:api')->get('routes', [ApiController::class, 'userroutes']);
Route::middleware('auth:api')->get('activeroutes', [ApiController::class, 'useractiveroutes']);
Route::get('rankingtravel', [ApiController::class, 'getRankingTravelDetails']);
Route::get('rankinglist', [ApiController::class, 'getRankingList']);
Route::middleware('auth:api')->post('radeemPoints', [ApiController::class, 'radeemPoints']);
Route::middleware('auth:api')->get('myranking', [ApiController::class, 'getMyRank']);
Route::middleware('auth:api')->get('monthly-co2', [ApiController::class, 'getMonthlyCo2']);
Route::middleware('auth:api')->get('get-my-routes', [ApiController::class, 'getMyRoutes']);


Route::get('ml/getcars', [ApiController::class, 'getCarsByUser']);
Route::get('/export-data', [DataExportController::class, 'exportToCsv']);
