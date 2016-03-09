#ifndef _PROTOCOL_TEST_H_
#define _PROTOCOL_TEST_H_

#include <stdint.h>
#include <string>
#include <vector>

using namespace std;

enum protocolType
{
	login = 1001,
	run = 1002,
	stop = 1003,
	logout = 1004
};

class Header {
	public:
		static const int HEADER_SIZE;
		uint32_t start;
		protocolType pro;
		Header();
		Header(char *buff);
		uint32_t serialize(char *buff);
		Header deserialize(char *buff);
};

const int Header::HEADER_SIZE = 24;

class Body {
	public:
		virtual uint32_t serialize(char *buff);
		//virtual Body serialize(char *buff);
		//Body();
};

class Frame {
	public:
		Header head;
		Body body;
		Frame();
		Frame(char *buff);
		uint32_t serialize(char *buff);
		Frame deserialize(char *buff);
};

class LoginBody : public Body
{
	public:
		string passwd;
		LoginBody();
		LoginBody(char *buff, uint32_t size);
		uint32_t serialize(char *buff);
		LoginBody deserialize(char *buff, uint32_t size);
};

class RunBody : public Body
{
public:
	class ParamItem {
		public:
			uint32_t param1;
			uint8_t param2;
			ParamItem();
			ParamItem(char *buff);
			uint32_t serialize(char *buff);
			ParamItem deserialize(char *buff);
	};
	vector<ParamItem> params;
	RunBody();
	RunBody(char *buff, uint32_t size);
	uint32_t serialize(char *buff);
	RunBody deserialize(char *buff, uint32_t size);
	~RunBody();
};

#endif //_PROTOCOL_TEST_H_