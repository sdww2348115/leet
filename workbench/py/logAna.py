import re

fin = open(r'test.txt', 'r')
fout = open(r'out.txt', 'w')
pattern = re.compile(r'pagehttps')

while True:
	line = fin.readline()
	if line == '':
		break;
	match = pattern.find(line)
	if match is not None:
		fout.write(line)

fin.close()
fout.close()		
