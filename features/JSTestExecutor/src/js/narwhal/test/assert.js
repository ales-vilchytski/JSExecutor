/*
Copyright (c) 2009, 280 North Inc. <[280north.com](http://280north.com/)\>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to
deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

// Tom Robison

var equiv = require("./equiv").equiv,
    jsDump = require("./jsdump").jsDump,
    util = require("../util");

var assert = exports;

function fail(message) {
    throw new AssertionError({
        "message": message
    });
}

assert.isTrue = function(assertion, message) {
    if (assertion !== true)
        fail((message || "") + "\nExpected true." +
            "\nActual = " + assertion);
}

assert.isFalse = function(assertion, message) {
    if (assertion !== false)
        fail((message || "") + "\nExpected false." +
            "\nActual = " + assertion);
}

assert.isNull = function(assertion, message) {
    if (assertion !== null)
        fail((message || "") + "\nExpected null." +
            "\nActual = " + assertion);
}

assert.isNaN = function(assertion, message) {
    if (!isNaN(assertion))
        fail((message || "") + "\nExpected NaN." +
            "\nActual = " + assertion);
}

assert.isEqual = function(expected, actual, message) {
    if (expected !== actual)
        fail((message || "") + "\nExpected equal to = " + jsDump.parse(expected) +
            "\nActual = " + jsDump.parse(actual));
}

assert.is = function (expected, actual, message) {
    if (!util.is(expected, actual))
        fail((message || "") + "\nExpected identical = " + jsDump.parse(expected) +
            "\nActual = " + jsDump.parse(actual));
};

assert.isSame = function(expected, actual, message) {
    if (!equiv(expected, actual))
        fail((message || "") + "\nExpected same as = " + jsDump.parse(expected) +
            "\nActual = " + jsDump.parse(actual));
}

assert.eq = function (expected, actual, message) {
    if (!util.eq(expected, actual))
        fail((message || "") + "\nExpected equal = " + jsDump.parse(expected) +
            "\nActual = " + jsDump.parse(actual));
};


assert.isDiff = function(expected, actual, message) {
    if (equiv(expected, actual))
        fail((message || "") + "\nExpected different than = " + jsDump.parse(expected) +
            "\nActual = " + jsDump.parse(actual));
}

assert.throwsError = function(block, type, message) {
    var threw = false,
        exception = null;
        
    try {
        block();
    } catch (e) {
        threw = true;
        exception = e;
    }
    
    if (!threw)
        fail("Expected exception" + (message ? ": " + message : ""));
    
    if (type !== undefined && !(exception instanceof type))
        fail("Expected exception type '"+type+
            "', actually '"+exception+"'" + (message ? ": " + message : ""));
}

var AssertionError = exports.AssertionError = require("assert").AssertionError;

AssertionError.prototype = new Error();
