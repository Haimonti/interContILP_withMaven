#include <stdio.h>
#define		FALSE	0
#define		TRUE	1

#define		RAND		rand()
#define		SRAND(x)	srand((x))
#define 	MYRAND(lo,hi) ((RAND%(((hi)-(lo))+1))+(lo))
/*
#define         RAND            ((random()&2147483647) / 2147483648.0)
#define         SRAND(x)        srandom((int)(x))
#define         MYRAND(lo,hi) ((RAND*(((hi)-(lo))+1)) + (lo))
*/


/*
#define		VERBOSE
*/
main(argc,argv)
	int argc;
	char *argv[];
	{
	int samplesize,nlines=0,newline=TRUE,printing=FALSE,
		comment=FALSE,start=cputime();
	long int t;
	FILE *in,*out1, *out2;
	char *inname,*outname1, *outname2;
	char c;

	if((argc < 4) || (argc > 6)) {
		printf("[usage: %s [seed] number_of_lines infile outfile1 outfile2]\n",argv[0]);
		exit(0);
	}
	if (argc == 6) {
		sscanf(argv[1],"%ld",&t);
	}
	else {
		time(&t);
	}
	SRAND((int)t);
	sscanf(argv[argc-4],"%d",&samplesize);

	outname2 = argv[argc-1]; outname1 = argv[argc-2]; inname = argv[argc-3];

	if (!(in=fopen(inname,"r"))) {
		printf("%s: no such file or directory\n",inname);
		exit(1);
	}
	out1=fopen(outname1,"w");
	out2=fopen(outname2,"w");
	while((c=fgetc(in))!=EOF) {
	    if(newline) {
		if(c=='%') comment=TRUE;
		newline=FALSE;
	    }
	    if(c=='\n') {
		if(!comment) nlines++;
		newline=TRUE;
		comment=FALSE;
	    }
	}
	if (argc == 4) samplesize = nlines/2;
	fclose(in); in=fopen(inname,"r");
	newline=TRUE; comment=FALSE; printing=FALSE;
#ifdef VERBOSE
	printf("[counted %d lines]\n",nlines);
	printf("[sampling %d from %d]\n",samplesize,nlines);
	printf("[seed %d]\n",(int)t);
#else
	printf("%d\n",(int)t);
#endif
	while((c=fgetc(in))!=EOF) {
	    if(newline) {
		if(c=='%') {printing=FALSE; comment=TRUE;}
		else if(MYRAND(1,nlines)<=samplesize) {
		    printing=TRUE;
		    samplesize--;
		}
		else printing=FALSE;
		newline=FALSE;
	    }
	    if(printing) fputc(c,out1);
	    else fputc(c,out2);
	    if(c=='\n') {
		if(!comment) nlines--;
		newline=TRUE;
		comment=FALSE;
	    }
	}
	fclose(in); fclose(out1); fclose(out2);
#ifdef VERBOSE
	printf("Time taken = %dms\n",cputime()-start);
#endif
}

#include <sys/types.h>
#include <sys/times.h>
#include <sys/time.h>

int
cputime()
	{
	struct tms buffer;
	times(&buffer);
	return((buffer.tms_stime+buffer.tms_utime)*16.6);
}
