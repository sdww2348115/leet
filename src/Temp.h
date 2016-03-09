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
		uint32_t start;
		protocolType pro;
		Header();
		/*{
			start = 0x12345678;
			pro = login;
		}*/
		Header(char *buff);
		/*{
			start = *((uint32_t)buff);
			pro = *((protocolType)(buff + sizeof(uint32_t)));
		}*/
		void serialize(char *buff);
		Header deserialize(char *buff);
};

class Body {
	public:
		virtual void serialize(char *buff) = 0;
		virtual Body serialize(char *buff) = 0;
};

class Frame {
	public:
		Header head;
		Body body;
		Frame();
		Frame(char *buff);
		void serialize(char *buff);
		Frame deserialize(char *buff);
};

class LoginBody : public Body
{
	public:
		string passwd;
		LoginBody();
		LoginBody(char *buff, uint32_t size);
		void serialize(char *buff);
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
			void serialize(char *buff);
			ParamItem deserialize(char *buff);
	};
	vector<ParamItem> params;
	RunBody();
	RunBody(char *buff, uint32_t size);
	void serialize(char *buff);
	RunBody deserialize(char *buff, uint32_t size);
	~RunBody();
};

#endif