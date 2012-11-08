#ifndef _GLFUNCTIONS_
#define _GLFUNCTIONS_

#include <assert.h>
#include <iostream>
#include <fstream>

#include <GL/glew.h>
#include <GL/gl.h>
#include <Cg/cg.h>
#include <Cg/cgGL.h>
#include <GL/glut.h>
#include <ncmath.hpp>
#include "image.hpp"
#include <Magick++.h>

using namespace Magick;
using namespace std;

#ifdef WIN32
#define NCGL_PATH "E:/Work/OpenGL/ncgl/"
#define NCGL_SHADER_PATH "E:/Work/OpenGL/ncgl/Shaders/"
#else
#define NCGL_PATH "/scratch/ncorneli/ncgl/"
#define NCGL_SHADER_PATH "/scratch/ncorneli/ncgl/Shaders/"
#endif

namespace ncgl
{
	static void
	setObjectTexGen(int index, nc_m4x4 TG)
	{
		glActiveTextureARB(GL_TEXTURE0_ARB+index);
		glTexGendv(GL_S, GL_OBJECT_PLANE, TG[0]);
		glTexGendv(GL_T, GL_OBJECT_PLANE, TG[1]);
		glTexGendv(GL_R, GL_OBJECT_PLANE, TG[2]);
		glTexGendv(GL_Q, GL_OBJECT_PLANE, TG[3]);
	}

	static void
	setEyeTexGen(int index, nc_m4x4 TG)
	{
		glActiveTextureARB(GL_TEXTURE0_ARB+index);
		glTexGendv(GL_S, GL_EYE_PLANE, TG[0]);
		glTexGendv(GL_T, GL_EYE_PLANE, TG[1]);
		glTexGendv(GL_R, GL_EYE_PLANE, TG[2]);
		glTexGendv(GL_Q, GL_EYE_PLANE, TG[3]);
	}

	static void
	activateObjectTexGen(int unit)
	{
		glActiveTextureARB(GL_TEXTURE0_ARB+unit);

		glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR);
		glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR);
		glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR);
		glTexGeni(GL_Q, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR);

		glEnable(GL_TEXTURE_GEN_S);
		glEnable(GL_TEXTURE_GEN_T);
		glEnable(GL_TEXTURE_GEN_R);
		glEnable(GL_TEXTURE_GEN_Q);
	}

	static void
	activateEyeTexGen(int unit)
	{
		glActiveTextureARB(GL_TEXTURE0_ARB+unit);

		glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
		glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
		glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
		glTexGeni(GL_Q, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);

		glEnable(GL_TEXTURE_GEN_S);
		glEnable(GL_TEXTURE_GEN_T);
		glEnable(GL_TEXTURE_GEN_R);
		glEnable(GL_TEXTURE_GEN_Q);
	}

	static void
	deactivateObjectTexGen(int unit)
	{
		glActiveTextureARB(GL_TEXTURE0_ARB+unit);

		glDisable(GL_TEXTURE_GEN_S);
		glDisable(GL_TEXTURE_GEN_T);
		glDisable(GL_TEXTURE_GEN_R);
		glDisable(GL_TEXTURE_GEN_Q);
	}

	static void
	deactivateEyeTexGen(int unit)
	{
		glActiveTextureARB(GL_TEXTURE0_ARB+unit);

		glDisable(GL_TEXTURE_GEN_S);
		glDisable(GL_TEXTURE_GEN_T);
		glDisable(GL_TEXTURE_GEN_R);
		glDisable(GL_TEXTURE_GEN_Q);
	}

	static void
	checkErrors()
	{
		GLenum error;
		while ((error = glGetError()) != GL_NO_ERROR) {
			fprintf(stderr, "Error: %s\n", (char *) gluErrorString(error));
		}
	}

	static GLuint
	loadFragmentProgram(char* fpname)
	{
		ifstream file(fpname);
		string tmp;
		char tmpchar;
		while (file.get(tmpchar))
			tmp+=tmpchar;
		file.close();
		GLuint fpID;
		glGenProgramsARB(1, &fpID);
		glBindProgramARB(GL_FRAGMENT_PROGRAM_ARB, fpID);
		glProgramStringARB(GL_FRAGMENT_PROGRAM_ARB, GL_PROGRAM_FORMAT_ASCII_ARB, (GLsizei)tmp.length(), (GLubyte *)tmp.c_str());
		//std::cerr << tmp.c_str() << endl;
		return fpID;
	}

	static GLuint
	loadVertexProgram(char* fpname)
	{
		ifstream file(fpname);
		string tmp;
		char tmpchar;
		while (file.get(tmpchar))
			tmp+=tmpchar;
		file.close();
		GLuint fpID;
		glGenProgramsARB(1, &fpID);
		glBindProgramARB(GL_VERTEX_PROGRAM_ARB, fpID);
		glProgramStringARB(GL_VERTEX_PROGRAM_ARB, GL_PROGRAM_FORMAT_ASCII_ARB, (GLsizei)tmp.length(), (GLubyte *)tmp.c_str());
		//std::cerr << tmp.c_str() << endl;
		return fpID;
	}

	static string
	stringReplace(string str,string in, string out)
	{
		string tmp;
		int valid=0;
		int nrvalid=(int)in.length();

		for (unsigned int pos=0;pos<str.length();)
		{
			while(pos+valid<str.length() && valid<nrvalid && str[pos+valid]==in[valid])
				++valid;
			if (valid==nrvalid)
			{
				tmp+=out;
				pos+=nrvalid;
			}
			else
			{
				tmp+=str[pos];
				++pos;
			}
			valid=0;
		}
		return tmp;
	}

	static void
	showTex(ncglImage& im, bool flip = false, GLenum filter = GL_NEAREST)
	{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		if (flip)
			glRotatef(180,1.0,0.0,0.0);

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_FRAGMENT_PROGRAM_ARB);

		glColor3f(1.0,1.0,1.0);
		glActiveTextureARB(GL_TEXTURE0_ARB);

		glBindTexture(im.getType(),im.getID());

		deactivateEyeTexGen(0);
		deactivateObjectTexGen(0);

		glTexParameteri(im.getType(), GL_TEXTURE_MIN_FILTER, filter);
		glTexParameteri(im.getType(), GL_TEXTURE_MAG_FILTER, filter);

		glEnable(im.getType());
		if (im.getType() == GL_TEXTURE_2D)
		{
			glBegin(GL_QUADS);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,0);
			glVertex2f(-1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,1,0);
			glVertex2f(1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,1,1);
			glVertex2f(1.0,1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,1);
			glVertex2f(-1.0,1.0);
			glEnd();
		}
		if (im.getType() == GL_TEXTURE_RECTANGLE_NV)
		{
			glBegin(GL_QUADS);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,0);
			glVertex2f(-1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,im.getWidth(),0);
			glVertex2f(1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,im.getWidth(),im.getHeight());
			glVertex2f(1.0,1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,im.getHeight());
			glVertex2f(-1.0,1.0);
			glEnd();
		}

		glDisable(im.getType());
	}

	static void
	showTex(ncglImage* im, bool flip = false, GLenum filter = GL_NEAREST)
	{
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		if (flip)
			glRotatef(180,1.0,0.0,0.0);

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_FRAGMENT_PROGRAM_ARB);

		glColor3f(1.0,1.0,1.0);
		glActiveTextureARB(GL_TEXTURE0_ARB);

		glBindTexture(im->getType(),im->getID());

		deactivateEyeTexGen(0);
		deactivateObjectTexGen(0);

		glTexParameteri(im->getType(), GL_TEXTURE_MIN_FILTER, filter);
		glTexParameteri(im->getType(), GL_TEXTURE_MAG_FILTER, filter);

		glEnable(im->getType());
		if (im->getType() == GL_TEXTURE_2D)
		{
			glBegin(GL_QUADS);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,0);
			glVertex2f(-1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,1,0);
			glVertex2f(1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,1,1);
			glVertex2f(1.0,1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,1);
			glVertex2f(-1.0,1.0);
			glEnd();
		}
		if (im->getType() == GL_TEXTURE_RECTANGLE_NV)
		{
			glBegin(GL_QUADS);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,0);
			glVertex2f(-1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,im->getWidth(),0);
			glVertex2f(1.0,-1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,im->getWidth(),im->getHeight());
			glVertex2f(1.0,1.0);
			glMultiTexCoord2iARB(GL_TEXTURE0_ARB,0,im->getHeight());
			glVertex2f(-1.0,1.0);
			glEnd();
		}

		glDisable(im->getType());
	}

	static void
	checkProgramError()
	{

		GLint error_pos;
		glGetIntegerv(GL_PROGRAM_ERROR_POSITION_ARB, &error_pos);
		if (error_pos != -1) {
			const GLubyte *error_string;
			error_string = glGetString(GL_PROGRAM_ERROR_STRING_ARB);
			fprintf(stderr, "Program error at position: %d\n%s\n", error_pos, error_string);
		}
	}

	static void
	CHECK_FRAMEBUFFER_STATUS()
	{
		GLenum status;
		status = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT);
		switch(status) {
			case GL_FRAMEBUFFER_COMPLETE_EXT:
				cerr << "PASSED" << endl;
				break;
			case GL_FRAMEBUFFER_UNSUPPORTED_EXT:
				cerr << "GL_FRAMEBUFFER_UNSUPPORTED_EXT" << endl;
				/* choose different formats */
				break;
			default:
				cerr << "FAILED" << endl;
			/* programming error; will fail on all hardware */
			assert(0);
		}
		cerr.flush();
	}

	static CGpass
	Validate(CGcontext context_, CGeffect effect_)
	{
		if (!effect_) {
			fprintf(stderr, "Unable to create effect!\n");
			const char *listing = cgGetLastListing(context_);
			if (listing)
				fprintf(stderr, "%s\n", listing);
			//exit(1);
		}

		CGtechnique technique = cgGetFirstTechnique(effect_);
		while (technique) {
			if (cgValidateTechnique(technique) == CG_FALSE)
				fprintf(stderr,"Technique %s did not validate. Skipping.\n",cgGetTechniqueName(technique));
			technique = cgGetNextTechnique(technique);
		}
		technique = cgGetFirstTechnique(effect_);
		while (!cgIsTechniqueValidated(technique))
			technique = cgGetNextTechnique(technique);
		if (!technique) {
			fprintf(stderr, "No valid techniques in effect file!  Exiting...\n");
			//exit(1);
		}

		return cgGetFirstPass(technique);
	}

	static void	errorCallback()
	{
		fprintf(stderr, "Cg error: %s\n", cgGetErrorString(cgGetError()));
		fflush(stderr);
	}

	static CGcontext
	createContext()
	{
		CGcontext context;
		context = cgCreateContext();
		cgSetErrorCallback(errorCallback);
		cgGLRegisterStates(context);
		cgGLSetOptimalOptions(CG_PROFILE_VP40);
		cgGLSetOptimalOptions(CG_PROFILE_FP40);
		return context;
	};

}

#endif
