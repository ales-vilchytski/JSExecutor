$.include('fields.js');

testCases(test,
	
	function checkInheritance() {
		var numF = new util.NumHolder();
		assert.that(numF, isA(util.Holder));
	},
	
	function checkHolderHolding() {
		var holder = new util.Holder('ohMyName');
		assert.that(holder.getName(), eq('ohMyName'));
		
		holder.setValue();
		assert.that(typeof holder.getValue(), eq('undefined'));
		
		
		holder.setValue(null);
		assert.that(holder.getValue(), eq(null));
		
		var obj = {};
		holder.setValue(obj);
		assert.that(holder.getValue(), eq(obj));
	},
	
	function checkNumericHolderNumeric() {
		var holder = new util.NumHolder('ohMyName', '1', '2.0', '3.050');
		assert.that(holder.getName(), eq('ohMyName'));	//inherited, but copy/paste is simpler
		assert.that(holder.getValue(), eq(1));
		
		holder.setValue('2.5');
		assert.that(holder.getValue(), eq(2.5));
		
		holder.setValue(2.6);
		assert.that(holder.getValue(), eq(2.6));
		
		shouldThrowException(function() { holder.setValue(0); });
		shouldThrowException(function() { holder.setValue(4); });
		shouldThrowException(function() { holder.setValue(); });
		shouldThrowException(function() { holder.setValue('asd'); });
		
		holder.setValue('2.6');
		assert.that(holder.getValue(), eq(2.6));
		
		//check reset to default
		holder.setValue(null);
		assert.that(holder.getValue(), eq(1));
		
		//check numeric but not string comparison of restrictions
		holder.setValue('2');
		assert.that(holder.getValue(), eq(2));
		holder.setValue('3.0500');
		assert.that(holder.getValue(), eq(3.05));
	}
);
