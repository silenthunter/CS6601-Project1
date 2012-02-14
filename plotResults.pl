use strict;

open(INPUT, $ARGV[0]);
open(FILE, ">$ARGV[0]_cleaned");
my $strat = "";
my $difficulty = "";
while(<INPUT>)
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
