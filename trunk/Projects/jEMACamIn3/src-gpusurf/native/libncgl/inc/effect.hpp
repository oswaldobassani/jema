#ifndef _NCGL_EFFECT_
#define _NCGL_EFFECT_

#include <GL/glew.h>
#include <GL/gl.h>
#include <Cg/cg.h>
#include <Cg/cgGL.h>
#include "string.hpp"
#include "image.hpp"
#include <vector>

class ncglEffect {
public:
	ncglEffect();
	~ncglEffect();

	static void Init();
    static void errorCallback();
    void load(const char* filename);
	void loadFromString(const char* datastring);
	void Validate();
	void SetupSampler(const char* paramname, ncglImage* im);
	void SetupSampler(const char* paramname, GLuint id);
	void SetCurrentMatrixParameter(const char* paramname,CGGLenum matrix);
	void SetMatrixParameterfr(const char* paramname,float* data);
	void SetMatrixParameterdr(const char* paramname,double* data);
	void SetParameter4dv(const char* paramname,double* data);
	void SetParameter4fv(const char* paramname,float* data);
	void SetParameter1f(const char* paramname,float f1);
	void SetParameter1i(const char* paramname,int i1);
	void SetParameter2f(const char* paramname,float f1,float f2);
	void SetParameter3f(const char* paramname,float f1,float f2,float f3);
	void SetParameter4f(const char* paramname,float f1,float f2,float f3,float f4);
	CGparameter getParameter(const char* paramname);

	void setPass(int index=0);
	void resetPass(int index=0);


private:
	CGeffect effect;
	std::vector<CGpass > passes;
    static CGcontext glc;
    static bool initialized;
};

#endif
