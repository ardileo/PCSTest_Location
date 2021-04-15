<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Support\Facades\Auth;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class UserController extends Controller
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

    /**
     * Get the authenticated User.
     *
     * @return Response
     */
    public function profile()
    {
        $out = [
            "code" => 200,
            "result" => [
                "user" => Auth::user(),
            ]
        ];

        return response()->json($out, $out['code']);
    }

    public function updateName(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name' => 'required|string',
        ]);

        if ($validator->fails()) {
            $out = [
                "code" => 409,
                "message" => $validator->errors()->first(),
            ];
            return response()->json($out, $out['code']);
        } else {
            $user = Auth::user();
            $user->name = $request->input('name');
            $user->save();

            $out = [
                "code" => 200,
                "result" => [
                    "user" => $user,
                ]
            ];

            return response()->json($out, $out['code']);
        }
    }
}
