package trees;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class FamilyTree {

    private static class TreeNode<T> {
        private T data;
        private TreeNode<T> parent;
        private ArrayList<TreeNode<T>> children;

        TreeNode(T data) {
            this.data = data;
            children = new ArrayList<>();
        }

        T getData() {
            return data;
        }

        void addChild(TreeNode<T> childNode) {
            childNode.parent = this;
            children.add(childNode);
        }


        TreeNode<T> getNodeWithName(String targetName) {
            if (data.equals(targetName))
                return this;
                    
            for (TreeNode<T> child: children)
            {
                TreeNode<T> foundNode = child.getNodeWithName(targetName);
                if (foundNode != null) {
                    return foundNode;
                }
            }
            
            return null;
        }


        ArrayList<TreeNode<T>> collectAncestorsToList() {
            ArrayList<TreeNode<T>> ancestors = new ArrayList<>();
            TreeNode<T> current = this.parent;
            while (current != null) {
                ancestors.add(current);
                current = current.parent;
            }
            return ancestors;
        }

        public String toString() {
            return toStringWithIndent("");
        }

        private String toStringWithIndent(String indent) {
            String s = indent + data + "\n";
            indent += "  ";
            for (TreeNode<T> childNode : children)
                s += childNode.toStringWithIndent(indent);
            return s;
        }
    }

    private TreeNode<String> root;


    public FamilyTree() throws IOException, TreeException {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Family tree text files", "txt");
        File dirf = new File("data");
        if (!dirf.exists()) dirf = new File(".");

        JFileChooser chooser = new JFileChooser(dirf);
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(1);
        File treeFile = chooser.getSelectedFile();


        FileReader fr = new FileReader(treeFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null)
            addLine(line);
        br.close();
        fr.close();
    }


    private void addLine(String line) throws TreeException
    {
        int colonIndex = line.indexOf(':');
        if (colonIndex < 0)
            throw new TreeException("Illegal line format: " + line);
        String parent = line.substring(0, colonIndex);
        String childrenString = line.substring(colonIndex + 1);
        String[] childrenArray = childrenString.split(",");
        

        TreeNode parentNode;
        if (root == null)
            parentNode = root = new TreeNode<>(parent);
        else
        {
            parentNode = root.getNodeWithName(parent);
            if (parentNode == null) throw new TreeException("Parent not found: " + parent);
        }
        
        for (String childName : childrenArray) {
            if (!childName.trim().isEmpty()) { // Handle empty strings if split results in one
                parentNode.addChild(new TreeNode<>(childName.trim()));
            }
        }
    }


    TreeNode<String> getMostRecentCommonAncestor(String name1, String name2) throws TreeException
    {
        TreeNode<String> node1 = root.getNodeWithName(name1);
        if (node1 == null)
            throw new TreeException("Node not found: " + name1);
        TreeNode<String> node2 = root.getNodeWithName(name2);
        if (node2 == null)
            throw new TreeException("Node not found: " + name2);
        
        ArrayList<TreeNode<String>> ancestorsOf1 = node1.collectAncestorsToList();
        ArrayList<TreeNode<String>> ancestorsOf2 = node2.collectAncestorsToList();
        

        for (TreeNode<String> n1: ancestorsOf1)
            if (ancestorsOf2.contains(n1))
                return n1;
        
        return null;
    }

    public String toString() {
        return "Family Tree:\n\n" + root;
    }

    public static void main(String[] args) {
        try {
            FamilyTree tree = new FamilyTree();
            System.out.println("Tree:\n" + tree + "\n**************\n");
        } catch (IOException x) {
            System.out.println("IO trouble: " + x.getMessage());
        } catch (TreeException x) {
            System.out.println("Input file trouble: " + x.getMessage());
        }
    }
}
