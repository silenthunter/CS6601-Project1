use strict;


open(FILE, ">graphData");
my $strat = "";
my $difficulty = "";
while(<>)
{
	my $line = $_;
	chomp $line;

	if($line =~ m/Game ended at move: (\d+)/)
	{
		print FILE $1."\n";
	}
	elsif($line =~ m/^Playing \d+ (\w+) games using strategy (.*)$/)
	{
		$strat = $1;
		$difficulty = $2;
	}
}
close(FILE);
system("./createGraph.eps graphData $strat $difficulty");
