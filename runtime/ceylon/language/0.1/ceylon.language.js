(function (define) {
    define('ceylon.language', function (require, exports) {

//the Ceylon language module
function print(line) { console.log(line.getString().value) }

CeylonObject=function CeylonObject() {}

CeylonObject.prototype.getString=function() { String(Object.prototype.toString.apply(this)) };
CeylonObject.prototype.toString=function() { return this.getString().value };

//TODO: we need to distinguish between Objects and IdentifiableObjects
CeylonObject.prototype.equals = function(other) { return Boolean(this===other) }

function $Integer() {}
function Integer(value) {
    var that = new $Integer;
    that.value = value;
    return that;
}
for(var $ in CeylonObject.prototype){$Integer.prototype[$]=CeylonObject.prototype[$]}
$Integer.prototype.getString = function() { return String(this.value.toString()) }
$Integer.prototype.plus = function(other) { return Integer(this.value+other.value) }
$Integer.prototype.minus = function(other) { return Integer(this.value-other.value) }
$Integer.prototype.times = function(other) { return Integer(this.value*other.value) }
$Integer.prototype.divided = function(other) {
    var exact = this.value/other.value;
    return Integer((exact<0) ? Math.ceil(exact) : Math.floor(exact));
}
$Integer.prototype.remainder = function(other) { return Integer(this.value%other.value) }
$Integer.prototype.power = function(other) {
    var exact = Math.pow(this.value, other.value);
    return Integer((exact<0) ? Math.ceil(exact) : Math.floor(exact));
}
$Integer.prototype.negativeValue = function() { return Integer(-this.value) }
$Integer.prototype.positiveValue = function() { return this }
$Integer.prototype.equals = function(other) { return Boolean(other.value===this.value) }
$Integer.prototype.compare = function(other) {
    return this.value===other.value ? equal
                                    : (this.value<other.value ? smaller:larger);
}
$Integer.prototype.getFloat = function() { return Float(this.value) }
$Integer.prototype.getInteger = function() { return this }
$Integer.prototype.getSuccessor = function() { return Integer(this.value+1) }
$Integer.prototype.getPredecessor = function() { return Integer(this.value-1) }

function $Float() {}
function Float(value) {
    var that = new $Float;
    that.value = value;
    return that;
}
for(var $ in CeylonObject.prototype){$Float.prototype[$]=CeylonObject.prototype[$]}
$Float.prototype.getString = function() { return String(this.value.toString()) }
$Float.prototype.plus = function(other) { return Float(this.value+other.value) }
$Float.prototype.minus = function(other) { return Float(this.value-other.value) }
$Float.prototype.times = function(other) { return Float(this.value*other.value) }
$Float.prototype.divided = function(other) { return Float(this.value/other.value) }
$Float.prototype.power = function(other) { return Float(Math.pow(this.value, other.value)) }
$Float.prototype.negativeValue = function() { return Float(-this.value) }
$Float.prototype.positiveValue = function() { return this }
$Float.prototype.equals = function(other) { return Boolean(other.value===this.value) }
$Float.prototype.compare = function(other) {
    return this.value===other.value ? equal
                                    : (this.value<other.value ? smaller:larger);
}
$Float.prototype.getFloat = function() { return this }

function $String() {}
function String(value) {
    var that = new $String;
    that.value = value;
    return that;
}
for(var $ in CeylonObject.prototype){$String.prototype[$]=CeylonObject.prototype[$]}
$String.prototype.getString = function() { return this }
$String.prototype.toString = function() { return this.value }
$String.prototype.plus = function(other) { return String(this.value+other.value) }
$String.prototype.equals = function(other) { return Boolean(other.value===this.value) }
$String.prototype.compare = function(other) {
    return this.value===other.value ? equal
                                    : (this.value<other.value ? smaller:larger);
}
$String.prototype.getUppercased = function() { return String(this.value.toUpperCase()) }
$String.prototype.getLowercased = function() { return String(this.value.toLowerCase()) }

function $Case() {}
function Case(caseName) {
    var that = new $Case;
    that.string = String(caseName);
    return that;
}
for(var $ in CeylonObject.prototype){$Case.prototype[$]=CeylonObject.prototype[$]}
$Case.prototype.getString = function() { return this.string }

function getNull() { return null }
var $true = Case("true");
function getTrue() { return $true; }
var $false = Case("false");
function getFalse() { return $false; }
function Boolean(value) {
    return value ? $true : $false;
}

//These are operators for handling nulls
function exists(value) { return value === getNull() ? getFalse() : getTrue(); }
function nonempty(value) { return value && value.value && value.value.length > 0 ? getTrue() : getFalse(); }

var larger = Case("larger");
function getLarger() { return larger }
var smaller = Case("smaller");
function getSmaller() { return smaller }
var equal = Case("equal");
function getEqual() { return equal }
function largest(x, y) { return x.compare(y) === larger ? x : y }
function smallest(x, y) { return x.compare(y) === smaller ? x : y }

function $ArraySequence() {}
function ArraySequence(value) {
    var that = new $ArraySequence;
    that.value = value;
    return that;
}
for(var $ in CeylonObject.prototype){$ArraySequence.prototype[$]=CeylonObject.prototype[$]}
$ArraySequence.prototype.getString = function() { return String(this.value.toString()) }
$ArraySequence.prototype.item = function(index) {
    var result = this.value[index.value];
    return result!==undefined ? result:null;
}
$ArraySequence.prototype.getSize = function() { return Integer(this.value.length) }

function $Singleton() {}
function Singleton(elem) {
    var that = new $Singleton;
    that.value = elem;
    return that;
}
for(var $ in CeylonObject.prototype){$Singleton.prototype[$]=CeylonObject.prototype[$]}
$Singleton.prototype.getString = function() { return String(this.value.toString()) }
$Singleton.prototype.item = function(index) {
    return index.value===0 ? this.value : null;
}
$Singleton.prototype.getSize = function() { return Integer(1) }

function $Entry() {}
function Entry(key, item) {
    var that = new $Entry;
    that.key = key;
    that.item = item;
    return that;
}
for(var $ in CeylonObject.prototype){$Entry.prototype[$]=CeylonObject.prototype[$]}
$Entry.prototype.getString = function() {
    return String(this.key.getString().value + "->" + this.item.getString().value)
}
$Entry.prototype.getKey = function() { return this.key }
$Entry.prototype.getItem = function() { return this.item }
$Entry.prototype.equals = function(other) {
    return Boolean(other && this.key.equals(other.key) && this.item.equals(other.item));
}
$Entry.prototype.getHash = function() { Integer(this.key.getHash().value ^ this.item.getHash().value) }

//receives ArraySequence, returns element
function min(seq) {
    var v = seq.value[0];
    if (seq.value.length > 1) {
        for (i = 1; i < seq.value.length; i++) {
            v = smallest(v, seq.value[i]);
        }
    }
    return v;
}
//receives ArraySequence, returns element 
function max(seq) {
    var v = seq.value[0];
    if (seq.value.length > 1) {
        for (i = 1; i < seq.value.length; i++) {
            v = largest(v, seq.value[i]);
        }
    }
    return v;
}
//receives ArraySequence of ArraySequences, returns flat ArraySequence
function join(seqs) {
    var builder = [];
    for (i = 0; i < seqs.value.length; i++) {
        builder = builder.concat(seqs.value[i].value);
    }
    return ArraySequence(builder);
}
//receives ArraySequences, returns ArraySequence
function zip(keys, items) {
    var entries = []
    var numEntries = Math.min(keys.value.length, items.value.length);
    for (i = 0; i < numEntries; i++) {
        entries[i] = Entry(keys.value[i], items.value[i]);
    }
    return ArraySequence(entries);
}
//receives and returns ArraySequence
function coalesce(seq) {
    var newseq = [];
    for (i = 0; i < seq.value.length; i++) {
        if (seq.value[i]) {
            newseq = newseq.concat(seq.value[i]);
        }
    }
    return ArraySequence(newseq);
}

//receives ArraySequence and CeylonObject, returns new ArraySequence
function append(seq, elem) {
    return ArraySequence(seq.value.concat(elem));
}

//Receives ArraySequence, returns ArraySequence (with Entries)
function entries(seq) {
    var e = [];
    for (i = 0; i < seq.value.length; i++) {
        e.push(Entry(Integer(i), seq.value[i]));
    }
    return ArraySequence(e);
}

exports.print=print;
exports.Integer=Integer;
exports.Float=Float;
exports.String=String;
exports.Boolean=Boolean;
exports.getNull=getNull;
exports.Case=Case;
exports.getTrue=getTrue;
exports.getFalse=getFalse;
exports.getLarger=getLarger;
exports.getSmaller=getSmaller;
exports.getEqual=getEqual;
exports.ArraySequence=ArraySequence;
exports.Singleton=Singleton;
exports.Entry=Entry;
exports.largest=largest;
exports.smallest=smallest;
exports.min=min;
exports.max=max;
exports.join=join;
exports.zip=zip;
exports.coalesce=coalesce;
exports.append=append;
exports.entries=entries;
exports.exists=exists;
exports.nonempty=nonempty;

    });
}(typeof define==='function' && define.amd ? 
    define : function (id, factory) {
    if (typeof exports!=='undefined') {
        factory(require, exports);
    } else {
        throw "no module loader";
    }
}));
