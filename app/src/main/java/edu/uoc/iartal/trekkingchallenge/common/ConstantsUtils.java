/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.common;

// Constants used by activities
public class ConstantsUtils {

    final public static int DEFAULT_RANKING_POSITION = 0;
    final public static int NUM_OF_DECIMALS = 2;
    final public static int DEFAULT_MEMBERS = 1;

    final public static String KEY_CIPHER = "TrekkingChallenge_TFG_iartal";
    final public static String ALGORITHM_AES = "AES";
    final public static String ALGORITHM_SHA_256 = "SHA-256";
    final public static String UTF_8 = "UTF-8";

    final public static String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]{2,})*$";
    final public static String DISTANCE_PATTERN = "^[0-9\"]+([.][0-9\"]+)?$";
}
