//check default usage of ContextFactory an DirRequireBuilder

//if require builder creates sandoxed require then module.uri is null
var dirRequireBuilderDefault = module.uri != null

//if ContextFactory is sandboxed it usually denies usage of getClass() 
//and classes out of 'java.lang.*' or specific application classes
var clazz = new org.mozilla.javascript.ContextFactory().getClass();

exports.success = dirRequireBuilderDefault && clazz != null;