:- [aleph].
:- [subtle].
:- use_module(library(terms)).
:- set(counter,0).

union(File1,File2):-
	add_features(File1),
	add_features(File2),
	listing('$aleph_feature'),
	listing('$aleph_feature_count').

add_features(File):-
	open(File,read,Stream),
	repeat,
	read(Stream,Fact),
	(Fact = end_of_file -> close(Stream);
		process(Fact),
		fail),
	setting(counter,C),
	retractall('$aleph_feature_count'(_)),
	asserta('$aleph_feature_count'(C)).

process(Fact):-
	functor(Fact,Name,_),
	Name \= '$aleph_feature', !.
process(Fact):-
	rehash_feature(Fact).

rehash_feature(Fact):-
	Fact = '$aleph_feature'(N1,A1,B1,Head1,Body1),
	clause_to_list((Head1:-Body1),CL1),
	hash_term(CL1,Hash),
	 \+ exists_hash(Hash), !,
	asserta('$aleph_feature'(Hash,A1,B1,Head1,Body1)),
	setting(counter,C),
	C1 is C + 1,
	set(counter,C1).
rehash_feature(_).

exists_hash(Hash):-
        '$aleph_feature'(Hash,_,_,_,_).
        
exists_equiv(CL1,Hash):-
	'$aleph_feature'(N0,A0,B0,Head0,Body0),
	clause_to_list((Head0:-Body0),CL0),
	equiv(CL0,CL1),
	(Hash < N0 ->
		retract('$aleph_feature'(N0,A0,B0,Head0,Body0)),
		asserta('$aleph_feature'(Hash,A0,B0,Head0,Body0));
		true).

hash_term(Term,Hash):-
	copy_term(Term,Copy),
	numbervars(Copy,0,_),
	term_hash(Copy,Hash).




