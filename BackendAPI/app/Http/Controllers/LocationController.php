<?php

namespace App\Http\Controllers;

use App\Models\Location;
use App\Models\User;
use Illuminate\Support\Facades\Auth;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class LocationController extends Controller
{
    /**
     * Instantiate a new UserController instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('auth');
    }

    public function allUserLocation()
    {
        $user = Auth::user();
        $out = [
            "code" => 200,
            "result" => [
                "locations" => $user->locations()->orderBy('id','desc')->get(),
            ]
        ];

        return response()->json($out, $out['code']);
    }

    public function add(Request $request)
    {
        $user = Auth::user();

        $validator = Validator::make($request->all(), [
            'longitude' => 'required|numeric',
            'latitude' => 'required|numeric',
        ]);

        if ($validator->fails()) {
            $out = [
                "code" => 409,
                "message" => $validator->errors()->first(),
            ];
            return response()->json($out, $out['code']);
        } else {
            $newLoc = new Location();
            $newLoc->user = $user->id;
            $newLoc->latitude = $request->input('latitude');
            $newLoc->longitude = $request->input('longitude');
            $newLoc->save();

            $out = [
                "code" => 200,
                "result" => [
                    "location" => $newLoc,
                ]
            ];
            return response()->json($out, $out['code']);
        }
    }

    public function delete(Request $request)
    {
        $user = Auth::user();

        $validator = Validator::make($request->all(), [
            'id' => 'required|exists:locations',
        ]);

        if ($validator->fails()) {
            $out = [
                "code" => 409,
                "message" => $validator->errors()->first(),
            ];
            return response()->json($out, $out['code']);
        } else {
            $location = Location::find($request->input('id'));
            $deleted = $location->delete();
            $out = [
                "code" => $deleted ? 200 : 409,
                "message" => $deleted ? "Deleted" : "Failed",
            ];
            return response()->json($out, $out['code']);
        }
    }
}
