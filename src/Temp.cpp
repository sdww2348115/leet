#include "Temp.h"
#include <stdio.h>
#include <cstring>

Header::Header() 
{
	this->pro = login;
}

uint32_t Header::serialize(char *buff)
{
	char *p = buff;
	memcpy(p, &this->start, sizeof(uint32_t));
	p += sizeof(uint32_t);
	memcpy(p, &this->pro, sizeof(protocolType));
	p += sizeof(protocolType);
	return p - buff;
}

int main(void)
{
	Header header;
	char a[50];
	uint32_t size = header.serialize(a);
	int i = 0;
	for(; i < size; i++)
	{
		printf("%d ", a[i]);
	}
	printf("%d \n", header.pro);
}