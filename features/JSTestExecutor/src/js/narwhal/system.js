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

// -- tlrobinson Tom Robinson

var io = require("./io");

exports.print = function () {
    exports.stdout.write(Array.prototype.join.call(arguments, ' ') + "\n").flush();
};

exports.stdin  = new io.TextInputStream(new io.IO(Packages.java.lang.System['in'], null));
exports.stdout = new io.TextOutputStream(new io.IO(null, Packages.java.lang.System.out));
exports.stderr = new io.TextOutputStream(new io.IO(null, Packages.java.lang.System.err));

exports.args = global.arguments || [];
exports.originalArgs = exports.args.slice(0);

exports.env = {};

// fetch ENV variables
var env = Packages.java.lang.System.getenv(),
    keyIterator = env.keySet().iterator();
while (keyIterator.hasNext()) {
    var key = keyIterator.next();
    exports.env[String(key)] = String(env.get(key));
}

// merge properties over top
var properties = Packages.java.lang.System.getProperties();
keyIterator = properties.keySet().iterator();
while (keyIterator.hasNext()) {
    var key = keyIterator.next();
    exports.env[String(key)] = String(properties.getProperty(key));
}


var securityManager = Packages.java.lang.System.getSecurityManager()
if (securityManager) {
    var securityManagerName = securityManager.getClass().getName();
    if (/^com.google.app(engine|hosting)./.test(securityManagerName))
        exports.appEngine = true;
    if (/^com.google.apphosting\./.test(securityManagerName))
        exports.appEngineHosting = true;
}

exports.fs = require('./file');

// default logger
var Logger = require("./logger").Logger;
exports.log = new Logger(exports.stderr);

