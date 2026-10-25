package com.lunarhavenx.client.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Expands Forge 1.8.9's logo quad to the full splash viewport and hides the
 * separate Forge emblem. If Forge changes the expected bytecode, this
 * transformer leaves the original class intact.
 */
public final class LunarHavenXSplashTransformer implements IClassTransformer {
   private static final String TARGET = "net.minecraftforge.fml.client.SplashProgress$3";
   private static final String READY_PROPERTY = "lunarhavenx.splash.ready";

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if(basicClass == null
              || (!TARGET.equals(name) && !TARGET.equals(transformedName))
              || !"true".equals(System.getProperty(READY_PROPERTY))) {
         return basicClass;
      }

      try {
         ClassNode type = new ClassNode();
         new ClassReader(basicClass).accept(type, 0);
         MethodNode run = null;
         for(MethodNode method : type.methods) {
            if("run".equals(method.name) && "()V".equals(method.desc)) {
               run = method;
               break;
            }
         }
         if(run == null) {
            return basicClass;
         }

         List<MethodInsnNode> vertices = new ArrayList<MethodInsnNode>();
         for(AbstractInsnNode instruction = run.instructions.getFirst();
               instruction != null; instruction = instruction.getNext()) {
            if(instruction instanceof MethodInsnNode) {
               MethodInsnNode call = (MethodInsnNode)instruction;
               if("org/lwjgl/opengl/GL11".equals(call.owner)
                       && "glVertex2f".equals(call.name)
                       && "(FF)V".equals(call.desc)) {
                  vertices.add(call);
               }
            }
         }

         // These eight calls are the four background corners and four
         // corners of Forge's separate spinning logo in Forge 11.15.1.2318.
         if(vertices.size() != 8) {
            return basicClass;
         }

         for(int i = 0; i < 4; ++i) {
            MethodInsnNode vertex = vertices.get(i);
            AbstractInsnNode yNode = previousCodeInstruction(vertex);
            AbstractInsnNode xNode = previousCodeInstruction(yNode);
            if(!(xNode instanceof LdcInsnNode) || !(yNode instanceof LdcInsnNode)) {
               return basicClass;
            }

            Object xValue = ((LdcInsnNode)xNode).cst;
            Object yValue = ((LdcInsnNode)yNode).cst;
            if(!(xValue instanceof Float) || !(yValue instanceof Float)) {
               return basicClass;
            }

            float originalX = ((Float)xValue).floatValue();
            float originalY = ((Float)yValue).floatValue();
            boolean right;
            boolean bottom;
            if(originalX == 64.0F) {
               right = false;
            } else if(originalX == 576.0F) {
               right = true;
            } else {
               return basicClass;
            }
            if(originalY == -16.0F) {
               bottom = false;
            } else if(originalY == 496.0F) {
               bottom = true;
            } else {
               return basicClass;
            }

            run.instructions.insertBefore(xNode, coordinateExpression(right, false));
            run.instructions.remove(xNode);
            run.instructions.insertBefore(yNode, coordinateExpression(bottom, true));
            run.instructions.remove(yNode);
         }

         // Prevent the stock Forge glyph from covering the center of the
         // user's full-screen artwork.
         for(int i = 4; i < 8; ++i) {
            InsnList collapse = new InsnList();
            collapse.add(new InsnNode(Opcodes.POP));
            collapse.add(new InsnNode(Opcodes.POP));
            collapse.add(new InsnNode(Opcodes.FCONST_0));
            collapse.add(new InsnNode(Opcodes.FCONST_0));
            run.instructions.insertBefore(vertices.get(i), collapse);
         }

          AbstractInsnNode backgroundEnd = vertices.get(3).getNext();
          while(backgroundEnd != null) {
             if(backgroundEnd instanceof MethodInsnNode) {
                MethodInsnNode call = (MethodInsnNode)backgroundEnd;
                if("org/lwjgl/opengl/GL11".equals(call.owner)
                        && "glEnd".equals(call.name)
                        && "()V".equals(call.desc)) {
                   break;
                }
             }
             backgroundEnd = backgroundEnd.getNext();
          }
          if(backgroundEnd == null) {
             return basicClass;
          }

          // The supplied artwork already contains its own progress bar.
          InsnList hideForgeBars = new InsnList();
          hideForgeBars.add(new InsnNode(Opcodes.ACONST_NULL));
          hideForgeBars.add(new VarInsnNode(Opcodes.ASTORE, 1));
          run.instructions.insert(backgroundEnd, hideForgeBars);

         ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
         type.accept(writer);
         return writer.toByteArray();
      } catch (Throwable ignored) {
         return basicClass;
      }
   }

   private static AbstractInsnNode previousCodeInstruction(AbstractInsnNode instruction) {
      AbstractInsnNode previous = instruction == null ? null : instruction.getPrevious();
      while(previous != null && previous.getOpcode() < 0) {
         previous = previous.getPrevious();
      }
      return previous;
   }

   private static InsnList coordinateExpression(boolean positive, boolean vertical) {
      InsnList expression = new InsnList();
      expression.add(new IntInsnNode(Opcodes.SIPUSH, vertical ? 240 : 320));
      expression.add(new VarInsnNode(Opcodes.ILOAD, vertical ? 6 : 5));
      expression.add(new InsnNode(Opcodes.ICONST_2));
      expression.add(new InsnNode(Opcodes.IDIV));
      expression.add(new InsnNode(positive ? Opcodes.IADD : Opcodes.ISUB));
      expression.add(new InsnNode(Opcodes.I2F));
      return expression;
   }
}
