<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ViewController;
use App\Http\Controllers\ActionController;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', [ViewController::class, 'login'])->name('index');
Route::get('/login', [ViewController::class, 'login'])->name('index.login');
Route::post('/login', [ActionController::class, 'login'])->name('login.submit');
Route::get('/home', [ViewController::class, 'home'])->name('home');

Route::get('/transmissiontype', [ViewController::class, 'transmissiontype'])->name('transmissiontype');
Route::get('/fueltype', [ViewController::class, 'fueltype'])->name('fueltype');
Route::get('/travel', [ViewController::class, 'travel'])->name('travel');
Route::get('/modeoftranspotations', [ViewController::class, 'modeoftranspotations'])->name('modeoftranspotations');

Route::get('/car', [ViewController::class, 'car'])->name('car');

Route::get('/createfueltype', [ViewController::class, 'createfueltype'])->name('create.fueltype.get');
Route::post('/postcreatefueltype', [ActionController::class, 'postcreatefueltype'])->name('post.create.fueltype');

Route::get('/createtransmissiontype', [ViewController::class, 'createtransmissiontype'])->name('create.transmission.type.get');
Route::post('/postcreatetransmissiontype', [ActionController::class, 'postcreatetransmissiontype'])->name('post.create.transmission.type');

Route::get('/createmodeoftranspotations', [ViewController::class, 'createmodeoftranspotations'])->name('create.mode.of.transpotations.get');
Route::post('/postcreatemodeoftranspotations', [ActionController::class, 'postcreatemodeoftranspotations'])->name('post.create.mode.of.transpotations');