
Fb : UGen {

	*delayUGen { ^DelayN }

	*new { arg func, maxdelaytime, delaytime=maxdelaytime, numChannels;
		var buf, phase, frames, sig, adddelay;
		if (maxdelaytime.isNil) {
			adddelay = false;
		} {
			adddelay = true;
			maxdelaytime = maxdelaytime - ControlDur.ir;
			delaytime = delaytime - ControlDur.ir;
		};
		
		numChannels = numChannels ?? { func.value(Silent.ar(1)).asArray.size };
		numChannels = numChannels max: maxdelaytime.asArray.size max: delaytime.asArray.size;
		
		frames = ControlDur.ir*SampleRate.ir;
		buf = LocalBuf(frames, numChannels).clear;
		phase = Phasor.ar(0, 1, 0, frames);

		sig = func.value(BufRd.ar(numChannels, buf, phase));
		if (adddelay) { sig = this.delayUGen.ar(sig, maxdelaytime, delaytime) };
		BufWr.ar(sig, buf, phase);
		^sig;
	}
}

FbL : Fb {*delayUGen{^DelayL}}

FbC : Fb {*delayUGen{^DelayC}}

Fb1 : UGen {

	*new { arg func, maxdelaytime, delaytime = maxdelaytime, numChannels;
		var buffers, in, out, write;
		var maxdelaysamples, delaysamples, readoffset, writeoffset;
		var sr = SampleRate.ir;
		
		if (maxdelaytime.isNil) {
			maxdelaysamples = 1;
			delaysamples = 0;
			readoffset = 0;
			writeoffset = 0;
		} {
			maxdelaysamples = sr * maxdelaytime - 1;
			delaysamples = { sr * delaytime.value - 1 };
			readoffset = { Dseries(0, 1, inf) % maxdelaysamples };
			writeoffset =  { Dseries(0, 1, inf) + delaysamples.value % maxdelaysamples };
		};
	
		numChannels = numChannels ?? { func.value(Silent.ar(1)).asArray.size };
		numChannels = numChannels max: maxdelaytime.asArray.size max: delaytime.asArray.size;
		
		buffers = { LocalBuf(maxdelaysamples + 1) } ! numChannels;
		in = Dbufrd(buffers, readoffset ! numChannels);
		out = func.value(in.unbubble);
		write = Dbufwr(out, buffers, writeoffset ! numChannels);
		Duty.ar(SampleDur.ir, 0, write)
	}
	
}

