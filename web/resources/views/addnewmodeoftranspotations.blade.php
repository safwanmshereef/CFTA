@extends('layout.app') 
@section('title') Mode of transpotations @endsection
@section('main')   
    <div class="content-wrapper">
        <div class="row">
            <div class="col-md-12 grid-margin stretch-card">
                <div class="card">
                    <div class="card-body">
                        <h4 class="card-title">Mode of transpotations</h4>
                        <p class="card-description"> Add New Mode of transpotations </p>
                        <form class="forms-sample"  action="{{ route('post.create.mode.of.transpotations') }}" method="POST">
                            @csrf
                            <div class="form-group">
                                <label for="exampleInputUsername1">Name</label>
                                <input type="text" class="form-control" id="exampleInputUsername1" placeholder="Name" name="name">
                            </div>
                            @error('transmission_type')
                                <div class="error">{{ $message }}</div>
                            @enderror
                            <button type="submit" class="btn btn-primary mr-2"> Submit </button>
                            <a href="{{ route('modeoftranspotations') }}" class="btn btn-light"> Cancel </a>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection      