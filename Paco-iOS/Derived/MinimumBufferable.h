//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/northropo/Projects/paco/Shared/src/com/pacoapp/paco/shared/model2/MinimumBufferable.java
//

#include "J2ObjC_header.h"

#pragma push_macro("MinimumBufferable_INCLUDE_ALL")
#ifdef MinimumBufferable_RESTRICT
#define MinimumBufferable_INCLUDE_ALL 0
#else
#define MinimumBufferable_INCLUDE_ALL 1
#endif
#undef MinimumBufferable_RESTRICT

#if !defined (PAMinimumBufferable_) && (MinimumBufferable_INCLUDE_ALL || defined(PAMinimumBufferable_INCLUDE))
#define PAMinimumBufferable_

@class JavaLangInteger;

@protocol PAMinimumBufferable < NSObject, JavaObject >

- (void)setMinimumBufferWithJavaLangInteger:(JavaLangInteger *)minBufferMinutes;

- (JavaLangInteger *)getMinimumBuffer;

@end

@interface PAMinimumBufferable : NSObject

@end

J2OBJC_STATIC_INIT(PAMinimumBufferable)

inline JavaLangInteger *PAMinimumBufferable_get_DEFAULT_MIN_BUFFER();
/*! INTERNAL ONLY - Use accessor function from above. */
FOUNDATION_EXPORT JavaLangInteger *PAMinimumBufferable_DEFAULT_MIN_BUFFER;
J2OBJC_STATIC_FIELD_OBJ_FINAL(PAMinimumBufferable, DEFAULT_MIN_BUFFER, JavaLangInteger *)

J2OBJC_TYPE_LITERAL_HEADER(PAMinimumBufferable)

#define ComPacoappPacoSharedModel2MinimumBufferable PAMinimumBufferable

#endif

#pragma pop_macro("MinimumBufferable_INCLUDE_ALL")